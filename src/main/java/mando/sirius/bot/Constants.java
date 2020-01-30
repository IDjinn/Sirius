package mando.sirius.bot;


public class Constants {
    public static String TOKEN = "";
    public static Long CURRENT_GUILD_ID = 0L;
    public static Long AUTH_CHANNEL = 0L;
    public static Long AUTH_ROLE = 0L;
    public static Long COUNTER_CHANNEL = 0L;
    public static String COUNTER_CHANNEL_NAME = "";
    public static Long EVENT_LOG_CHANNEL = 0L;
    public static boolean SYNC_BAN_ENABLED = true;
    public static boolean SYNC_USERNAME_ENABLED = true;
    public static Long LOG_CHANNEL_ID = 0L;

    public static String CREATE_USERS_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS `sirius_users` (\n" +
            "    `habbo_id` INT(11) NOT NULL,\n" +
            "    `user_id` VARCHAR(20) NOT NULL,\n" +
            "    PRIMARY KEY (`habbo_id`),\n" +
            "    UNIQUE INDEX `user_id` (`user_id`),\n" +
            "    CONSTRAINT `Habbo` FOREIGN KEY (`habbo_id`) REFERENCES `users` (`id`)\n" +
            ")";
    public static String CREATE_TEXTS_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS `sirius_texts` (\n" +
            "\t`key` VARCHAR(100) NOT NULL,\n" +
            "\t`value` VARCHAR(4096) NULL DEFAULT NULL\n" +
            ")";
    public static String CREATE_SETTINGS_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS `sirius_settings` (\n" +
            "\t`key` VARCHAR(100) NOT NULL,\n" +
            "\t`value` VARCHAR(4096) NULL DEFAULT NULL\n" +
            ")";
}
