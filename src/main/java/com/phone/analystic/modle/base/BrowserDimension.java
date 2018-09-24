/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: BrowserDimension
 * Author:   14751
 * Date:     2018/9/20 0:54
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间         版本号            描述
 */
package com.phone.analystic.modle.base;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * 〈一句话功能简述〉<br> 
 * 〈浏览器维度〉
 *
 * @author 14751
 * @create 2018/9/20 
 * @since 1.0.0
 */
public class BrowserDimension extends BaseDimension {
    private int id;
    private String browserName;
    private String browserVserion;

    public BrowserDimension() {
    }

    public BrowserDimension(String browserName, String browserVserion) {
        this.browserName = browserName;
        this.browserVserion = browserVserion;
    }

    public BrowserDimension(int id, String browserName, String browserVserion) {
        this(browserName,browserVserion);
        this.id = id;
    }

    //用于获取当前类的实例对象
    public static BrowserDimension newInstance(String browserName, String browserVserion){
        BrowserDimension browserDimension = new BrowserDimension();
        browserDimension.browserName = browserName;
        browserDimension.browserVserion = browserVserion;
        return browserDimension;
    }

//    /**
//     * 构造浏览器维度的集合对象,应用场景---当有两个属性决定一个维度的时候
//     * 例如：需求---查询某个浏览器下的所有版本
//     * @param browserName
//     * @param browserVserion
//     * @return
//     */
//    public static List<BrowserDimension> buildList(String browserName, String browserVserion ){
//        List<BrowserDimension> li = new ArrayList<>();
//        if(StringUtils.isEmpty(browserName)){
//            browserName = browserVserion = GlobalConstants.DEFAULT_VALUE;
//        }
//
//        if(StringUtils.isEmpty(browserVserion)){
//            browserVserion = GlobalConstants.DEFAULT_VALUE;
//        }
//
//        li.add(newInstance(browserName,browserVserion));
//        li.add(newInstance(browserName,GlobalConstants.ALL_OF_VALUE));
//        return li;
//    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeInt(id);
        dataOutput.writeUTF(browserName);//writeUTF是用来序列化字符串的
        dataOutput.writeUTF(browserVserion);
    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {
        this.id = dataInput.readInt();
        this.browserName = dataInput.readUTF();
        this.browserVserion = dataInput.readUTF();
    }

    @Override
    public int compareTo(BaseDimension o) {
        if(o == this){
            return 0;
        }
        BrowserDimension other = (BrowserDimension) o;
        int tmp = this.id - other.id;
        if(tmp != 0){
            return tmp;
        }
        tmp = this.browserName.compareTo(other.browserName);
        if(tmp != 0){
            return tmp;
        }
        tmp = this.browserVserion.compareTo(other.browserVserion);
        return tmp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BrowserDimension that = (BrowserDimension) o;

        if (id != that.id) return false;
        if (browserName != null ? !browserName.equals(that.browserName) : that.browserName != null) return false;
        return browserVserion != null ? browserVserion.equals(that.browserVserion) : that.browserVserion == null;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (browserName != null ? browserName.hashCode() : 0);
        result = 31 * result + (browserVserion != null ? browserVserion.hashCode() : 0);
        return result;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBrowserName() {
        return browserName;
    }

    public void setBrowserName(String browserName) {
        this.browserName = browserName;
    }

    public String getBrowserVserion() {
        return browserVserion;
    }

    public void setBrowserVserion(String browserVserion) {
        this.browserVserion = browserVserion;
    }
}