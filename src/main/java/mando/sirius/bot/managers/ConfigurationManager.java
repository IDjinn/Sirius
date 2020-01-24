package mando.sirius.bot.managers;

import com.eu.habbo.Emulator;

import java.sql.*;
import java.util.Map;
import java.util.Properties;

public class ConfigurationManager {
    private final Properties properties;
    public boolean isLoading = false;

    public ConfigurationManager() {
        this.properties = new Properties();
        this.reload();
    }

    public void reload() {
        this.isLoading = true;
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); Statement statement = connection.createStatement()) {
            if (statement.execute("SELECT * FROM `sirius_settings`")) {
                try (ResultSet set = statement.getResultSet()) {
                    while (set.next()) {
                        this.properties.put(set.getString("key"), set.getString("value"));
                    }
                    this.isLoading = false;
                }
            }
        } catch (SQLException e) {
            Emulator.getLogging().logSQLException(e);
        }
    }

    public void saveToDatabase() {
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("UPDATE `sirius_settings` SET `value` = ? WHERE `key` = ? LIMIT 1")) {
            for (Map.Entry<Object, Object> entry : this.properties.entrySet()) {
                statement.setString(1, entry.getValue().toString());
                statement.setString(2, entry.getKey().toString());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            Emulator.getLogging().logSQLException(e);
        }
    }


    public String getValue(String key) {
        return this.getValue(key, "");
    }


    public String getValue(String key, String defaultValue) {
        if (this.isLoading)
            return defaultValue;

        if (!this.properties.containsKey(key)) {
            Emulator.getLogging().logErrorLine("[SIRIUS CONFIG] Key not found: " + key);
        }
        return this.properties.getProperty(key, defaultValue);
    }


    public boolean getBoolean(String key) {
        return this.getBoolean(key, false);
    }


    public boolean getBoolean(String key, boolean defaultValue) {
        if (this.isLoading)
            return defaultValue;

        try {
            return (this.getValue(key, "0").equals("1")) || (this.getValue(key, "false").equals("true"));
        } catch (Exception e) {
            Emulator.getLogging().logErrorLine("[SIRIUS CONFIG] Failed to parse key " + key + " with value " + this.getValue(key) + " to type boolean.");
        }
        return defaultValue;
    }


    public int getInt(String key) {
        return this.getInt(key, 0);
    }


    public int getInt(String key, Integer defaultValue) {
        if (this.isLoading)
            return defaultValue;

        try {
            return Integer.parseInt(this.getValue(key, defaultValue.toString()));
        } catch (Exception e) {
            Emulator.getLogging().logErrorLine("[SIRIUS CONFIG] Failed to parse key " + key + " with value " + this.getValue(key) + " to type integer.");
        }
        return defaultValue;
    }


    public double getDouble(String key) {
        return this.getDouble(key, 0.0);
    }


    public double getDouble(String key, Double defaultValue) {
        if (this.isLoading)
            return defaultValue;

        try {
            return Double.parseDouble(this.getValue(key, defaultValue.toString()));
        } catch (Exception e) {
            Emulator.getLogging().logErrorLine("[SIRIUS CONFIG] Failed to parse key " + key + " with value " + this.getValue(key) + " to type double.");
        }

        return defaultValue;
    }


    public void update(String key, String value) {
        this.properties.setProperty(key, value);
    }

    public void register(String key, String value) {
        if (this.properties.getProperty(key, null) != null)
            return;

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("INSERT INTO `sirius_settings` VALUES (?, ?)")) {
            statement.setString(1, key);
            statement.setString(2, value);
            statement.execute();
        } catch (SQLException e) {
            Emulator.getLogging().logSQLException(e);
        }

        this.update(key, value);
    }
}
