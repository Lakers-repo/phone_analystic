/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: LocalOutputValue
 * Author:   14751
 * Date:     2018/9/27 2:01
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间         版本号            描述
 */
package com.phone.analystic.modle.value.reduce;

import com.phone.analystic.modle.value.StatsOutPutValue;
import com.phone.common.KpiType;
import org.apache.hadoop.io.WritableUtils;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * 〈一句话功能简述〉<br> 
 * 〈地域模块reduce阶段下的value的输出类型〉
 *
 * @author 14751
 * @create 2018/9/27 
 * @since 1.0.0
 */
public class LocalOutputValue extends StatsOutPutValue {
    private int activeUser;//活跃用户个数
    private int sessions;//回话总个数（去重）
    private int boundSessions;  //跳出会话个数
    private KpiType kpi;

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeInt(this.activeUser);
        dataOutput.writeInt(this.sessions);
        dataOutput.writeInt(this.boundSessions);
        WritableUtils.writeEnum(dataOutput,kpi);//枚举序列化（注意点）
    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {
        this.activeUser = dataInput.readInt();
        this.sessions = dataInput.readInt();
        this.boundSessions = dataInput.readInt();
        WritableUtils.readEnum(dataInput,KpiType.class);
    }

    public int getActiveUser() {
        return activeUser;
    }

    public void setActiveUser(int activeUser) {
        this.activeUser = activeUser;
    }

    public int getSessions() {
        return sessions;
    }

    public void setSessions(int sessions) {
        this.sessions = sessions;
    }

    public int getBoundSessions() {
        return boundSessions;
    }

    public void setBoundSessions(int boundSessions) {
        this.boundSessions = boundSessions;
    }

    @Override
    public KpiType getKpi() {
        return this.kpi;
    }

    public void setKpi(KpiType kpi) {
        this.kpi = kpi;
    }
}