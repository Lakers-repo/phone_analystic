/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: TimeOutPutValue
 * Author:   14751
 * Date:     2018/9/20 14:49
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间         版本号            描述
 */
package com.phone.analystic.modle.value.map;

import com.phone.analystic.modle.value.StatsOutPutValue;
import com.phone.common.KpiType;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * 〈一句话功能简述〉<br> 
 * 〈map端输出的value的类型〉
 *
 * @author 14751
 * @create 2018/9/20 
 * @since 1.0.0
 */
public class TimeOutPutValue extends StatsOutPutValue {
    private String id;//对id的泛指，可以是uuid，可以是umid，可以是sessionId
    private long time;//时间戳---求session时长

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeUTF(this.id);
        dataOutput.writeLong(this.time);
    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {
        this.id = dataInput.readUTF();
        this.time = dataInput.readLong();
    }

    @Override
    //这里返回空，说明在map输出value时 kpi值为空
    public KpiType getKpi() {
        return null;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}