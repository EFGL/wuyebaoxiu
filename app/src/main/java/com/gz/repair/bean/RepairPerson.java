package com.gz.repair.bean;

import java.util.ArrayList;

/**
 * 派工 - 维修人员列表
 * <p/>
 * Created by Endeavor on 2016/8/29.
 */
public class RepairPerson {


    public String timestamp;
    public int ret;
    public String msg;
    public ArrayList<Result> result;

    public class Result {

        public int id;
        public String name;

    }

}


