package mando.sirius.bot.managers;

import com.eu.habbo.Emulator;

import java.sql.*;
import java.util.Properties;

public class TextsManager {

    private final Properties texts;

    public TextsManager() {
        this.texts = new Properties();

        try {
            this.reload();
        } catch (Exception e) {
            Emulator.getLogging().logErrorLine(e);
        }
    }


    public void reload() throws Exception {
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); Statement statement = connection.createStatement(); ResultSet set = statement.executeQuery("SELECT * FROM `sirius_texts`")) {
            while (set.next()) {
                if (this.texts.containsKey(set.getString("key"))) {
                    this.texts.setProperty(set.getString("key"), set.getString("value"));
                } else {
                    this.texts.put(set.getString("key"), set.getString("value"));
                }
            }
        } catch (SQLException e) {
            Emulator.getLogging().logSQLException(e);
        }
    }


    public String getValue(String key) {
        return this.getValue(key, "");
    }


    public String getValue(String key, String defaultValue) {
        if (!this.texts.containsKey(key)) {
            Emulator.getLogging().logErrorLine("[SIRIUS TEXTS] Text key not found: " + key);
        }
        return this.texts.getProperty(key, defaultValue);
    }


    public boolean getBoolean(String key) {
        return this.getBoolean(key, false);
    }


    public boolean getBoolean(String key, Boolean defaultValue) {
        try {
            return (this.getValue(key, "0").equals("1")) || (this.getValue(key, "false").equals("true"));
        } catch (Exception e) {
            Emulator.getLogging().logErrorLine(e);
        }
        return defaultValue;
    }


    public int getInt(String key) {
        return this.getInt(key, 0);
    }


    public int getInt(String key, Integer defaultValue) {
        try {
            return Integer.parseInt(this.getValue(key, defaultValue.toString()));
        } catch (Exception e) {
            Emulator.getLogging().logErrorLine(e);
        }
        return defaultValue;
    }


    public void update(String key, String value) {
        this.texts.setProperty(key, value);
    }

    public void register(String key, String value) {
        if (this.texts.getProperty(key, null) != null)
            return;

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("INSERT INTO `sirius_texts` VALUES (?, ?)")) {
            statement.setString(1, key);
            statement.setString(2, value);
            statement.execute();
        } catch (SQLException e) {
            Emulator.getLogging().logSQLException(e);
        }

        this.update(key, value);
    }
}
