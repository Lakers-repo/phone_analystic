/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: TextOutputValue
 * Author:   14751
 * Date:     2018/9/27 2:00
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
 * 〈地域模块的map阶段的value输出类型〉
 *
 * @author 14751
 * @create 2018/9/27
 * @since 1.0.0
 */
public class TextOutputValue extends StatsOutPutValue {
    private String uuid = "";
    private String sessionId = "";

    @Override
    public KpiType getKpi() {
        return null;
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeUTF(this.uuid);
        dataOutput.writeUTF(this.sessionId);
    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {
        this.uuid = dataInput.readUTF();
        this.sessionId = dataInput.readUTF();
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}