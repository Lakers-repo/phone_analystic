1、数据抽取(抽取该模块中需要的字段即可)：
load data inpath '/ods/09/22' into table logs partition(month=09,day=22);

2、考虑是否需要udf函数(kpi)：
对于本项目可以不需要---可以写死（mysql数据库中）
如果维度较多，必须创建udf


3、创建最终结果表表：
CREATE TABLE IF NOT EXISTS `stats_view_depth` (
  `platform_dimension_id` int,
  `data_dimension_id` int,
  `kpi_dimension_id` int,
  `pv1` int,
  `pv2` int,
  `pv3` int,
  `pv4` int,
  `pv5_10` int,
  `pv10_30` int,
  `pv30_60` int,
  `pv60plus` int,
  `created` string
  );

4、创建临时表：--- 注意点
CREATE TABLE IF NOT EXISTS `stats_view_depth_tmp` (
 dt string,
 pl string,
 col string,
 ct int
);

5、sql语句（重点）

set hive.exec.mode.local.auto=true;
set hive.groupby.skewindata=true;
from(
select
from_unixtime(cast(l.s_time/1000 as bigint),"yyyy-MM-dd") as dt,
l.pl as pl,
l.u_ud as uid,
(case
when count(l.p_url) = 1 then "pv1"
when count(l.p_url) = 2 then "pv2"
when count(l.p_url) = 3 then "pv3"
when count(l.p_url) = 4 then "pv4"
when count(l.p_url) < 10 then "pv5_10"
when count(l.p_url) < 30 then "pv10_30"
when count(l.p_url) < 60 then "pv30_60"
else "pv60plus"
end) as pv
from phone l
where month = 09
and day = 19
and l.p_url <> 'null'
and l.pl is not null
group by from_unixtime(cast(l.s_time/1000 as bigint),"yyyy-MM-dd"),pl,u_ud
) as tmp
insert overwrite table stats_view_depth_tmp
select dt,pl,pv,count(distinct uid) as ct
where uid is not null
group by dt,pl,pv
;

样本数据：UUID需要去重
--》
    2018-09-19 website A pv1
    2018-09-19 website B pv1
    2018-09-19 website C pv1
    2018-09-19 website D pv2
    2018-09-19 website E pv2
    2018-09-19 website F pv2
    2018-09-19 website G pv2
--》
    2018-09-19 website pv1 3
    2018-09-19 website pv2 4

//with tmp as是创建一个临时表，数据存放在内存中，后面不能直接根据“；”，否则数据会消失，后面查询不会有结果
//后面紧跟着from或者在查询语句中跟from
set hive.exec.mode.local.auto=true;
set hive.groupby.skewindata=true;
with tmp as(
select dt,pl as pl,ct as pv1,0 as pv2,0 as pv3,0 as pv4,0 as pv5_10,0 as pv10_30,0 as pv30_60,0 as pv60plus from stats_view_depth_tmp where col = 'pv1' union all
select dt,pl as pl,0 as pv1,ct as pv2,0 as pv3,0 as pv4,0 as pv5_10,0 as pv10_30,0 as pv30_60,0 as pv60plus from stats_view_depth_tmp where col = 'pv2' union all
select dt,pl as pl,0 as pv1,0 as pv2,ct as pv3,0 as pv4,0 as pv5_10,0 as pv10_30,0 as pv30_60,0 as pv60plus from stats_view_depth_tmp where col = 'pv3' union all
select dt,pl as pl,0 as pv1,0 as pv2,0 as pv3,ct as pv4,0 as pv5_10,0 as pv10_30,0 as pv30_60,0 as pv60plus from stats_view_depth_tmp where col = 'pv4' union all
select dt,pl as pl,0 as pv1,0 as pv2,0 as pv3,0 as pv4,ct as pv5_10,0 as pv10_30,0 as pv30_60,0 as pv60plus from stats_view_depth_tmp where col = 'pv5_10' union all
select dt,pl as pl,0 as pv1,0 as pv2,0 as pv3,0 as pv4,0 as pv5_10,ct as pv10_30,0 as pv30_60,0 as pv60plus from stats_view_depth_tmp where col = 'pv10_30' union all
select dt,pl as pl,0 as pv1,0 as pv2,0 as pv3,0 as pv4,0 as pv5_10,0 as pv10_30,ct as pv30_60,0 as pv60plus from stats_view_depth_tmp where col = 'pv30_60' union all
select dt,pl as pl,0 as pv1,0 as pv2,0 as pv3,0 as pv4,0 as pv5_10,0 as pv10_30,0 as pv30_60,ct as pv60plus from stats_view_depth_tmp where col = 'pv60plus'
)
from tmp
insert overwrite table stats_view_depth
select phone_date(dt),phone_platform(pl),2,sum(pv1),sum(pv2),sum(pv3),sum(pv4),sum(pv5_10),sum(pv10_30),sum(pv30_60),sum(pv60pluss),dt
group by dt,pl
;

样本数据
--》
    2018-09-19 website  3 0 0 0 0 0 0 0
    2018-09-19 website  0 4 0 0 0 0 0 0

--》
    字段：date platform KPI pv1 pv2 pv3 pv4 pv5 pv5_10 pv10_30 pv30_60 pv60plus
    1 1 2 3 4 0 0 0 0 0 0


6、编写sqoop语句：
sqoop export --connect jdbc:mysql://hadoop03:3306/result \
 --username root --password mysql \
 --table stats_view_depth --export-dir hdfs://hadoop02:8020/hive/phone.db/stats_view_depth/* \
 --input-fields-terminated-by "\\001" --update-mode allowinsert \
 --update-key platform_dimension_id,date_dimension_id,kpi_dimension_id \
 ;

