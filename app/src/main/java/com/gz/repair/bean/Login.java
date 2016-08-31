package com.gz.repair.bean;

import java.util.ArrayList;

/**
 * Created by Endeavor on 2016/8/26.
 */


public class Login {


    public String timestamp;
    public int ret;
    public String msg;
    public Result result;

    public class Result {

        public String user_name;
        public int root_id;
        public int com_id;
        public int user_id;
        public ArrayList<Privileges> privileges;

        public class Privileges {

            public String privilege_name;
            public String privilege_id;

            @Override
            public String toString() {
                return "Privileges{" +
                        "privilege_name='" + privilege_name + '\'' +
                        ", privilege_id='" + privilege_id + '\'' +
                        '}';
            }

        }

        @Override
        public String toString() {
            return "Result{" +
                    "user_name='" + user_name + '\'' +
                    ", root_id=" + root_id +
                    ", com_id=" + com_id +
                    ", user_id=" + user_id +
                    ", privileges=" + privileges +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "Login{" +
                "timestamp='" + timestamp + '\'' +
                ", ret=" + ret +
                ", msg='" + msg + '\'' +
                ", result=" + result +
                '}';
    }
}



