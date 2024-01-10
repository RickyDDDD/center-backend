package com.ricky.personcenter.once;

import com.alibaba.excel.EasyExcel;

import java.util.List;

/**
 * 导入 Excel数据
 *
 * @author Ricky
 * @date 2024/01/04
 */
public class ImportExcel {

    public static void main(String[] args) {
        String fileName = "D:\\Code-Repository\\person-center\\center-SpringBoot\\src\\main\\resources\\testExcel.xlsx";
        readByListenner(fileName);
        synchronousRead(fileName);
    }

    /**
     * 监听器读
     *
     * @param fileName 文件名
     */
    public static void readByListenner(String fileName){
        EasyExcel.read(fileName, XingqiuTableUserInfo.class, new TableListener()).sheet().doRead();
    }

    /**
     * 同步读取
     *
     * @param fileName 文件名
     */
    public static void synchronousRead(String fileName){
        //这里需要指定读哪个class去读，然后读取第一个Sheet 同步读取会自动finish
        List<XingqiuTableUserInfo> totalDataList = EasyExcel
                .read(fileName).head(XingqiuTableUserInfo.class).sheet().doReadSync();
        for (XingqiuTableUserInfo xingqiuTableUserInfo : totalDataList) {
            System.out.println(xingqiuTableUserInfo);

        }
    }
}
