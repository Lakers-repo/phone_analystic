/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: PlatformDimension
 * Author:   14751
 * Date:     2018/9/20 1:35
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间         版本号            描述
 */
package com.phone.analystic.modle.base;

import com.phone.common.GlobalConstants;
import org.datanucleus.util.StringUtils;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * 〈一句话功能简述〉<br> 
 * 〈平台维度〉
 *
 * @author 14751
 * @create 2018/9/20 
 * @since 1.0.0
 */
public class PlatformDimension extends BaseDimension {
    private int id;
    private String platformName;

    public PlatformDimension() {
    }

    public PlatformDimension(String platformName) {
        this.platformName = platformName;
    }

    public PlatformDimension(int id, String platformName) {
        this(platformName);
        this.id = id;
    }

    //    /**
//     * 构建平台维度的集合对象
//     */
//    public static List<PlatformDimension> buildList(String platformName){
//        if(StringUtils.isEmpty(platformName)){
//            platformName = GlobalConstants.DEFAULT_VALUE;
//        }
//        List<PlatformDimension> li = new ArrayList<>();
//        li.add(new PlatformDimension(platformName));
//        li.add(new PlatformDimension(GlobalConstants.DEFAULT_VALUE));
//        return li;
//
//    }

    public static PlatformDimension getInstnce(String platformName){
        String pl = StringUtils.isEmpty(platformName) ? GlobalConstants.DEFAULT_VALUE :platformName;
        return new PlatformDimension(pl);
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeInt(id);
        dataOutput.writeUTF(platformName);
    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {
        this.id = dataInput.readInt();
        this.platformName = dataInput.readUTF();
    }

    @Override
    public int compareTo(BaseDimension o) {
       if(o == this){
           return 0;
       }

       PlatformDimension other = (PlatformDimension) o;
       int tmp = this.id - other.id;
       if(tmp != 0){
           return tmp;
       }
       tmp = this.platformName.compareTo(other.platformName);
       return tmp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PlatformDimension that = (PlatformDimension) o;

        if (id != that.id) return false;
        return platformName != null ? platformName.equals(that.platformName) : that.platformName == null;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (platformName != null ? platformName.hashCode() : 0);
        return result;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPlatformName() {
        return platformName;
    }

    public void setPlatformName(String platformName) {
        this.platformName = platformName;
    }
}