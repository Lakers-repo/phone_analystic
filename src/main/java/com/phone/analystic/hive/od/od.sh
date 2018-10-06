#!/bin/bash

##### ./od.sh -d 2018-09-23

dt=''
#循环运行时所带的参数
until [ $# -eq 0 ]
do
if [ $1'x' = '-dx' ]
then
shift
dt=$1
fi
shift
done

month=
day=
#判断日期是否合法和正常，获取字符串长度${#dt}
if [ ${#dt} = 10 ]
then
echo "dt:$dt"
else
dt=`date -d "1 days ago" "+%Y-%m-%d"`
fi

#计算month和day
month=`date -d "$dt" "+%m"`
day=`date -d "$dt" "+%d"`
echo "running date is:$dt,month is:$month,day is:$day"
echo "running hive SQL statment..."

#run hive hql1
hive --database log_phone -e "
set hive.exec.mode.local.auto=true;
set hive.groupby.skewindata=true;
with tmp as(
select
from_unixtime(cast(l.s_time/1000 as bigint),"yyyy-MM-dd") as dt,
l.pl as pl,
l.cut as cut,
l.pt as pt,
l.en as en,
if((case when l.en = 'e_crt' then count(distinct l.oid) end) is null,0,(case when l.en = 'e_crt' then count(distinct l.oid) end))as orders,
if((case when l.en = 'e_cs' then count(distinct l.oid) end) is null,0,(case when l.en = 'e_cs' then count(distinct l.oid) end))as success_orders,
if((case when l.en = 'e_cr' then count(distinct l.oid) end) is null,0,(case when l.en = 'e_cr' then count(distinct l.oid) end))as refund_orders
from phone l
where l.month = 09
and day = 23
and l.oid is not null
and l.oid <> 'null'
group by from_unixtime(cast(l.s_time/1000 as bigint),"yyyy-MM-dd"),pl,cut,pt,l.en
)
from(
select pl as pl ,dt as dt1,cut as cut,pt as pt,orders as orders,0 as success_orders,0 as refund_orders,dt from tmp where en = 'e_crt'
union all
select pl as pl ,dt as dt1,cut as cut,pt as pt,0 as orders,success_orders as success_orders,0 as refund_orders,dt from tmp where en = 'e_cs'
union all
select pl as pl ,dt as dt1,cut as cut,pt as pt,0 as orders,0 as success_orders,refund_orders as refund_orders,dt from tmp where en = 'e_cr'
) as tmp1
insert overwrite table stats_order_tmp4
select phone_platform(pl),phone_date(dt1),phone_currency(cut),phone_pay(pt),sum(orders),sum(success_orders),sum(refund_orders),dt1
group by pl,dt1,cut,pt
"

#run sqoop statment1
sqoop export --connect jdbc:mysql://hadoop03:3306/result \
--username root --password mysql \
--table stats_order --export-dir /hive/phone.db/stats_order_tmp4/* \
--input-fields-terminated-by "\\001" --update-mode allowinsert \
--update-key platform_dimension_id,date_dimension_id,currency_type_dimension_id,payment_type_dimension_id \
--columns 'platform_dimension_id,date_dimension_id,currency_type_dimension_id,payment_type_dimension_id,orders,success_orders,refund_orders,created' \
;

#run hive hql2
hive --database log_phone -e "
set hive.exec.mode.local.auto=true;
set hive.groupby.skewindata=true;
from(
select
from_unixtime(cast(l.s_time/1000 as bigint),"yyyy-MM-dd") as dt,
l.pl as pl,
l.cut as cut,
l.pt as pt,
if((case when l.en = 'e_crt' then sum(l.cua) end) is null,0,(case when l.en = 'e_crt' then sum(l.cua) end))as orders_amount,
if((case when s.en = 'e_cs' then sum(l.cua) end) is null,0,(case when s.en = 'e_cs' then sum(l.cua) end))as success_orders_amount,
if((case when r.en = 'e_cr' then sum(l.cua) end) is null,0,(case when r.en = 'e_cr' then sum(l.cua) end))as refund_orders_amount
from phone l
left join phone s
on s.o_id = l.o_id and s.en = 'e_cs'
left join phone r
on r.o_id = s.o_id and r.en = 'e_cr'
where l.month = 09
and l.day = 23
and l.o_id is not null
and l.o_id <> 'null'
and l.en = 'e_crt'
group by from_unixtime(cast(l.s_time/1000 as bigint),"yyyy-MM-dd"),l.pl,l.cut,l.pt,l.en,s.en,r.en
) as tmp
insert overwrite table stats_order_tmp5
select phone_platform(pl),phone_date(dt),phone_currency(cut),phone_pay(pt),sum(orders_amount),sum(success_orders_amount),sum(refund_orders_amount),dt
group by pl,dt,cut,pt
"

#run sqoop statment2
sqoop export --connect jdbc:mysql://hadoop03:3306/result \
--username root --password mysql -m 1 \
--table stats_order --export-dir /hive/phone.db/stats_order_tmp5/* \
--input-fields-terminated-by "\\01" --update-mode allowinsert \
--update-key platform_dimension_id,date_dimension_id,currency_type_dimension_id,payment_type_dimension_id \
--columns 'platform_dimension_id,date_dimension_id,currency_type_dimension_id,payment_type_dimension_id,order_amount,revenue_amount,refund_amount,created' \
;

echo "the event job is fininshed."