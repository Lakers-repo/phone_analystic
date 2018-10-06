1、编写事件的维度类和修改操作基础维度服务
2、编写udf函数（首先需要创建库，在对应库里面创建函数，否则调用函数需要加上库名）

create function phone_date as 'com.phone.analystic.hive.DateDimensionUDF' using jar 'hdfs://hadoop02:8020/logs/udfjars/phone_analystic-1.0.jar';
create function phone_event as 'com.phone.analystic.hive.EventDimensionUDF' using jar 'hdfs://hadoop02:8020/logs/udfjars/phone_analystic-1.0.jar';
create function phone_platform as 'com.phone.analystic.hive.PlatformDimensionUDF' using jar 'hdfs://hadoop02:8020/logs/udfjars/phone_analystic-1.0.jar';

3、创键元数据对应的临时表：
（orc格式的数据不可以直接用load的方式加载（orc到textfile---文本格式---表目录），
除非我们在清洗数据的时候，存储到hdfs时，就设置为orc格式，否则需要创建临时表）
create external table if not exists phone_tmp(
ver string,
s_time string,
en string,
u_ud string,
u_mid string,
u_sd string,
c_time string,
l string,
b_iev string,
b_rst string,
p_url string,
p_ref string,
tt string,
pl string,
ip String,
oid String,
`on` String,
cua String,
cut String,
pt String,
ca String,
ac String,
kv_ String,
du String,
browserName String,
browserVersion String,
osName String,
osVersion String,
country String,
province String,
city string
)
partitioned by(month string,day string)
;

load data inpath '/ods/09/22' into table phone_tmp partition(month=09,day=22);

4、创建hive表：（记住一定要是外部表）
create external table if not exists phone(
ver string,
s_time string,
en string,
u_ud string,
u_mid string,
u_sd string,
c_time string,
l string,
b_iev string,
b_rst string,
p_url string,
p_ref string,
tt string,
pl string,
ip String,
oid String,
`on` String,
cua String,
cut String,
pt String,
ca String,
ac String,
kv_ String,
du String,
browserName String,
browserVersion String,
osName String,
osVersion String,
country String,
province String,
city string
)
partitioned by(month string,day string)
stored as orc
;

//设置运行模式为本地---查询速度快
set hive.exec.mode.local.auto=true;


from phone_tmp
insert into phone partition(month=09,day=22)
select
ver,
s_time,
en,
u_ud,
u_mid,
u_sd,
c_time,
l,
b_iev,
b_rst,
p_url,
p_ref,
tt,
pl,
ip,
oid,
`on`,
cua,
cut,
pt,
ca,
ac,
kv_,
du,
browserName,
browserVersion,
osName,
osVersion,
country,
province,
city
where month = 9
and day = 22
;

5、在hive中创建和mysql最终结果便一样的临时表：
CREATE TABLE if not exists `stats_event` (
  `platform_dimension_id` int,
  `date_dimension_id` int,
  `event_dimension_id` int,
  `times` int,
  `created` String
)
;


语句：
set hive.exec.mode.local.auto=true;
set hive.groupby.skewindata=true;//如果数据繁盛倾斜，优化group by
//cast用于类型转换，因为from_unixtime方法第一个参数需要bigint类型
//from_unixtime --- 将long型的时间戳转换成Date格式的时间，unix_timestamp --- 将Date格式的时间转换long型的时间戳
from(
select
from_unixtime(cast(p.s_time/1000 as bigint),"yyyy-MM-dd") as dt,
p.pl as pl,
p.ca as ca,
p.ac as ac,
count(*) as ct
from phone p
where p.month = 9
and p.day = 22
and en = 'e_l'
group by from_unixtime(cast(p.s_time/1000 as bigint),"yyyy-MM-dd"),p.pl,p.ca,p.ac
) as tmp
insert overwrite table stats_event
select phone_platform(pl),phone_date(dt),phone_event(ca,ac),ct,dt
;

扩展维度：(了解)
set hive.exec.mode.local.auto=true;
set hive.groupby.skewindata=true;
with tmp as(
select
from_unixtime(cast(l.s_time/1000 as bigint),"yyyy-MM-dd") as dt,
l.pl as pl,
l.ca as ca,
l.ac as ac
from phone l
where month = 9
and day = 22
and l.en = 'e_l'
and l.s_time <> 'null'
)
from (
select dt as dt,pl as pl,ca as ca,ac as ac,count(1) as ct from tmp group by dt,pl,ca,ac union all
select dt as dt,pl as pl,ca as ca,'all' as ac,count(1) as ct from tmp group by dt,pl,ca union all
select dt as dt,'all' as pl,ca as ca,ac as ac,count(1) as ct from tmp group by dt,ca,ac union all
select dt as dt,'all' as pl,ca as ca,'all' as ac,count(1) as ct from tmp group by dt,ca
) as tmp1
insert overwrite table stats_event
select phone_date(dt),phone_platform(pl),phone_event(ca,ac),sum(ct),'2018-09-19'
group by pl,dt,ca,ac
;

6、sqoop导出数据
sqoop export --connect jdbc:mysql://hadoop03:3306/result \
--username root --password mysql -m 1 \
--table stats_event --export-dir hdfs://hadoop02:8020/hive/phone.db/stats_event/* \
--input-fields-terminated-by "\\001" --update-mode allowinsert \
--update-key date_dimension_id,platform_dimension_id,event_dimension_id \
;
