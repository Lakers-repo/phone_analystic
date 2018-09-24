/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: StatsOutPutValue
 * Author:   14751
 * Date:     2018/9/20 14:40
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间         版本号            描述
 */
package com.phone.analystic.modle.value;

import com.phone.common.KpiType;
import org.apache.hadoop.io.Writable;

/**
 * 〈一句话功能简述〉<br> 
 * 〈负责封装map或者是reduce阶段的输出的value的类型的顶级父类〉
 *
 * @author 14751
 * @create 2018/9/20 
 * @since 1.0.0
 */
public abstract class StatsOutPutValue implements Writable{
    //获取Kpi的抽象方法
    public abstract KpiType getKpi();
}