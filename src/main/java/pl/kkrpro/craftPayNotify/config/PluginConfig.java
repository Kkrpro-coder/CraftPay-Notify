package pl.kkrpro.craftPayNotify.config;

import eu.okaeri.configs.OkaeriConfig;
public class PluginConfig extends OkaeriConfig {

    public Mysql mysql = new Mysql();

    public static class Mysql extends OkaeriConfig {
        public String host = "ent-8.icehost.pl:5891";
        public int port = 5891;
        public String database = "s127871_Mongo";
        public String user = "u127871_yKWZmU3HcV";
        public String password = "+KSPS.ELjoq=7WcZpAGbef4m";
        public int poolSize = 5;
    }
}
