/*
 * ItemJoin
 * Copyright (C) CraftationGaming <https://www.craftationgaming.com/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package me.RockinChaos.itemjoin.utils.api;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.zip.GZIPOutputStream;
import javax.net.ssl.HttpsURLConnection;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

/**
* bStats Metrics Collection
* Collects some data for plugin authors.
*/
public class MetricsAPI {

  private final Plugin plugin;

  private final MetricsBase metricsBase;

 /**
  * Creates a new Metrics instance.
  *
  * @param plugin - Your plugin instance.
  * @param serviceId - The id of the service. It can be found at <a
  *     href="https://bstats.org/what-is-my-plugin-id">What is my plugin id?</a>
  */
  @SuppressWarnings("deprecation") // Header deprecated as of 1.18
  public MetricsAPI(final JavaPlugin plugin, final int serviceId) {
    this.plugin = plugin;
    File bStatsFolder = new File(plugin.getDataFolder().getParentFile(), "bStats");
    File configFile = new File(bStatsFolder, "config.yml");
    YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
    if (!config.isSet("serverUuid")) {
      config.addDefault("enabled", true);
      config.addDefault("serverUuid", UUID.randomUUID().toString());
      config.addDefault("logFailedRequests", false);
      config.addDefault("logSentData", false);
      config.addDefault("logResponseStatusText", false);
      config
          .options()
          .header(
              "bStats (https://bStats.org) collects some basic information for plugin authors, like how\n"
                  + "many people use their plugin and their total player count. It's recommended to keep bStats\n"
                  + "enabled, but if you're not comfortable with this, you can turn this setting off. There is no\n"
                  + "performance penalty associated with having metrics enabled, and data sent to bStats is fully\n"
                  + "anonymous.")
          .copyDefaults(true);
      try {
        config.save(configFile);
      } catch (IOException ignored) {
      }
    }
    boolean enabled = config.getBoolean("enabled", true);
    String serverUUID = config.getString("serverUuid");
    boolean logErrors = config.getBoolean("logFailedRequests", false);
    boolean logSentData = config.getBoolean("logSentData", false);
    boolean logResponseStatusText = config.getBoolean("logResponseStatusText", false);
    this.metricsBase =
        new MetricsBase(
            "bukkit",
            serverUUID,
            serviceId,
            enabled,
            this::appendPlatformData,
            this::appendServiceData,
            submitDataTask -> Bukkit.getScheduler().runTask(plugin, submitDataTask),
            plugin::isEnabled,
            (message, error) -> this.plugin.getLogger().log(Level.WARNING, message, error),
            (message) -> this.plugin.getLogger().log(Level.INFO, message),
            logErrors,
            logSentData,
            logResponseStatusText);
  }

 /**
  * Adds a custom chart.
  *
  * @param chart - The chart to add.
  */
  public void addCustomChart(final CustomChart chart) {
    this.metricsBase.addCustomChart(chart);
  }

 /**
  * Adds the Platform Data.
  *
  * @param builder - The JsonObjectBuilder.
  */
  private void appendPlatformData(final JsonObjectBuilder builder) {
    builder.appendField("playerAmount", this.getPlayerAmount());
    builder.appendField("onlineMode", Bukkit.getOnlineMode() ? 1 : 0);
    builder.appendField("bukkitVersion", Bukkit.getVersion());
    builder.appendField("bukkitName", Bukkit.getName());
    builder.appendField("javaVersion", System.getProperty("java.version"));
    builder.appendField("osName", System.getProperty("os.name"));
    builder.appendField("osArch", System.getProperty("os.arch"));
    builder.appendField("osVersion", System.getProperty("os.version"));
    builder.appendField("coreCount", Runtime.getRuntime().availableProcessors());
  }

 /**
  * Adds the Service Data.
  *
  * @param builder - The JsonObjectBuilder.
  */
  private void appendServiceData(final JsonObjectBuilder builder) {
    builder.appendField("pluginVersion", this.plugin.getDescription().getVersion());
  }

 /**
  * Gets the Player Count.
  * 
  * @return The number of Online Players.
  */
  private int getPlayerAmount() {
    try {
      Method onlinePlayersMethod = Class.forName("org.bukkit.Server").getMethod("getOnlinePlayers");
      return onlinePlayersMethod.getReturnType().equals(Collection.class)
          ? ((Collection<?>) onlinePlayersMethod.invoke(Bukkit.getServer())).size()
          : ((Player[]) onlinePlayersMethod.invoke(Bukkit.getServer())).length;
    } catch (Exception e) {
      return Bukkit.getOnlinePlayers().size();
    }
  }

 /**
  * MetricsBase Data Handling.
  */
  public static class MetricsBase {

    /** The version of the Metrics class. */
    public static final String METRICS_VERSION = "2.2.1";

    private static final ScheduledExecutorService scheduler =
        Executors.newScheduledThreadPool(1, task -> new Thread(task, "bStats-Metrics"));

    private static final String REPORT_URL = "https://bStats.org/api/v2/data/%s";

    private final String platform;

    private final String serverUuid;

    private final int serviceId;

    private final Consumer<JsonObjectBuilder> appendPlatformDataConsumer;

    private final Consumer<JsonObjectBuilder> appendServiceDataConsumer;

    private final Consumer<Runnable> submitTaskConsumer;

    private final Supplier<Boolean> checkServiceEnabledSupplier;

    private final BiConsumer<String, Throwable> errorLogger;

    private final Consumer<String> infoLogger;

    private final boolean logErrors;

    private final boolean logSentData;

    private final boolean logResponseStatusText;

    private final Set<CustomChart> customCharts = new HashSet<>();

    private final boolean enabled;

   /**
    * Creates a new MetricsBase class instance.
    *
    * @param platform The platform of the service.
    * @param serviceId The id of the service.
    * @param serverUuid The server uuid.
    * @param enabled Whether or not data sending is enabled.
    * @param appendPlatformDataConsumer A consumer that receives a {@code JsonObjectBuilder} and
    *     appends all platform-specific data.
    * @param appendServiceDataConsumer A consumer that receives a {@code JsonObjectBuilder} and
    *     appends all service-specific data.
    * @param submitTaskConsumer A consumer that takes a runnable with the submit task. This can be
    *     used to delegate the data collection to a another thread to prevent errors caused by
    *     concurrency. Can be {@code null}.
    * @param checkServiceEnabledSupplier A supplier to check if the service is still enabled.
    * @param errorLogger A consumer that accepts log message and an error.
    * @param infoLogger A consumer that accepts info log messages.
    * @param logErrors Whether or not errors should be logged.
    * @param logSentData Whether or not the sent data should be logged.
    * @param logResponseStatusText Whether or not the response status text should be logged.
    */
    public MetricsBase(
        String platform,
        String serverUuid,
        int serviceId,
        boolean enabled,
        Consumer<JsonObjectBuilder> appendPlatformDataConsumer,
        Consumer<JsonObjectBuilder> appendServiceDataConsumer,
        Consumer<Runnable> submitTaskConsumer,
        Supplier<Boolean> checkServiceEnabledSupplier,
        BiConsumer<String, Throwable> errorLogger,
        Consumer<String> infoLogger,
        boolean logErrors,
        boolean logSentData,
        boolean logResponseStatusText) {
      this.platform = platform;
      this.serverUuid = serverUuid;
      this.serviceId = serviceId;
      this.enabled = enabled;
      this.appendPlatformDataConsumer = appendPlatformDataConsumer;
      this.appendServiceDataConsumer = appendServiceDataConsumer;
      this.submitTaskConsumer = submitTaskConsumer;
      this.checkServiceEnabledSupplier = checkServiceEnabledSupplier;
      this.errorLogger = errorLogger;
      this.infoLogger = infoLogger;
      this.logErrors = logErrors;
      this.logSentData = logSentData;
      this.logResponseStatusText = logResponseStatusText;
      this.checkRelocation();
      if (enabled) {
        this.startSubmitting();
      }
    }
    
   /**
    * Adds a custom chart.
    *
    * @param chart - The chart to add.
    */
    public void addCustomChart(CustomChart chart) {
      this.customCharts.add(chart);
    }

   /**
    * Starts the Scheduler which submits our data every 30 minutes.
    */
    private void startSubmitting() {
      final Runnable submitTask =
          () -> {
            if (!this.enabled || !this.checkServiceEnabledSupplier.get()) {
              scheduler.shutdown();
              return;
            }
            if (this.submitTaskConsumer != null) {
              this.submitTaskConsumer.accept(this::submitData);
            } else {
              this.submitData();
            }
          };
      long initialDelay = (long) (1000 * 60 * (3 + Math.random() * 3));
      long secondDelay = (long) (1000 * 60 * (Math.random() * 30));
      scheduler.schedule(submitTask, initialDelay, TimeUnit.MILLISECONDS);
      scheduler.scheduleAtFixedRate(
          submitTask, initialDelay + secondDelay, 1000 * 60 * 30, TimeUnit.MILLISECONDS);
    }

   /**
    * Collects the data and sends it afterwards.
    */
    private void submitData() {
      final JsonObjectBuilder baseJsonBuilder = new JsonObjectBuilder();
      this.appendPlatformDataConsumer.accept(baseJsonBuilder);
      final JsonObjectBuilder serviceJsonBuilder = new JsonObjectBuilder();
      this.appendServiceDataConsumer.accept(serviceJsonBuilder);
      JsonObjectBuilder.JsonObject[] chartData =
          this.customCharts.stream()
              .map(customChart -> customChart.getRequestJsonObject(this.errorLogger, this.logErrors))
              .filter(Objects::nonNull)
              .toArray(JsonObjectBuilder.JsonObject[]::new);
      serviceJsonBuilder.appendField("id", this.serviceId);
      serviceJsonBuilder.appendField("customCharts", chartData);
      baseJsonBuilder.appendField("service", serviceJsonBuilder.build());
      baseJsonBuilder.appendField("serverUUID", this.serverUuid);
      baseJsonBuilder.appendField("metricsVersion", METRICS_VERSION);
      JsonObjectBuilder.JsonObject data = baseJsonBuilder.build();
      scheduler.execute(
          () -> {
            try {
              this.sendData(data);
            } catch (Exception e) {
              if (this.logErrors) {
                this.errorLogger.accept("Could not submit bStats metrics data", e);
              }
            }
          });
    }

   /**
    * Sends the data to the bStats server.
    *
    * @param data - The data to send.
    * @throws Exception If the request failed.
    */
    private void sendData(final JsonObjectBuilder.JsonObject data) throws Exception {
      if (this.logSentData) {
        this.infoLogger.accept("Sent bStats metrics data: " + data.toString());
      }
      String url = String.format(REPORT_URL, this.platform);
      HttpsURLConnection connection = (HttpsURLConnection) new URL(url).openConnection();
      byte[] compressedData = compress(data.toString());
      connection.setRequestMethod("POST");
      connection.addRequestProperty("Accept", "application/json");
      connection.addRequestProperty("Connection", "close");
      connection.addRequestProperty("Content-Encoding", "gzip");
      connection.addRequestProperty("Content-Length", String.valueOf(compressedData.length));
      connection.setRequestProperty("Content-Type", "application/json");
      connection.setRequestProperty("User-Agent", "Metrics-Service/1");
      connection.setDoOutput(true);
      try (DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream())) {
        outputStream.write(compressedData);
      }
      StringBuilder builder = new StringBuilder();
      try (BufferedReader bufferedReader =
          new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
        String line;
        while ((line = bufferedReader.readLine()) != null) {
          builder.append(line);
        }
      }
      if (this.logResponseStatusText) {
        this.infoLogger.accept("Sent data to bStats and received response: " + builder);
      }
    }

   /** 
    * Checks that the class was properly relocated. 
    *
    */
    private void checkRelocation() {
      if (System.getProperty("bstats.relocatecheck") == null
          || !System.getProperty("bstats.relocatecheck").equals("false")) {
        final String defaultPackage =
            new String(new byte[] {'o', 'r', 'g', '.', 'b', 's', 't', 'a', 't', 's'});
        final String examplePackage =
            new String(new byte[] {'y', 'o', 'u', 'r', '.', 'p', 'a', 'c', 'k', 'a', 'g', 'e'});
        if (MetricsBase.class.getPackage().getName().startsWith(defaultPackage)
            || MetricsBase.class.getPackage().getName().startsWith(examplePackage)) {
          throw new IllegalStateException("bStats Metrics class has not been relocated correctly!");
        }
      }
    }

   /**
    * Gzips the given string.
    *
    * @param str - The string to gzip.
    * @return The gzipped string.
    */
    private static byte[] compress(final String str) throws IOException {
      if (str == null) {
        return null;
      }
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      try (GZIPOutputStream gzip = new GZIPOutputStream(outputStream)) {
        gzip.write(str.getBytes(StandardCharsets.UTF_8));
      }
      return outputStream.toByteArray();
    }
  }

 /**
  * AdvancedPieChart Data Handling.
  */
  public static class AdvancedBarChart extends CustomChart {

    private final Callable<Map<String, int[]>> callable;

   /**
    * Class constructor.
    *
    * @param chartId - The id of the chart.
    * @param callable - The callable which is used to request the chart data.
    */
    public AdvancedBarChart(final String chartId, final Callable<Map<String, int[]>> callable) {
      super(chartId);
      this.callable = callable;
    }

   /**
    * Gets the ChartData.
    *
    * @return The JsonObjectBuilder.
    */
    @Override
    protected JsonObjectBuilder.JsonObject getChartData() throws Exception {
      JsonObjectBuilder valuesBuilder = new JsonObjectBuilder();
      Map<String, int[]> map = this.callable.call();
      if (map == null || map.isEmpty()) {
        return null;
      }
      boolean allSkipped = true;
      for (Map.Entry<String, int[]> entry : map.entrySet()) {
        if (entry.getValue().length == 0) {
          continue;
        }
        allSkipped = false;
        valuesBuilder.appendField(entry.getKey(), entry.getValue());
      }
      if (allSkipped) {
        return null;
      }
      return new JsonObjectBuilder().appendField("values", valuesBuilder.build()).build();
    }
  }

 /**
  * SimplePieChart Data Handling.
  */
  public static class SimpleBarChart extends CustomChart {

    private final Callable<Map<String, Integer>> callable;

   /**
    * Class constructor.
    *
    * @param chartId The id of the chart.
    * @param callable The callable which is used to request the chart data.
    */
    public SimpleBarChart(final String chartId, final Callable<Map<String, Integer>> callable) {
      super(chartId);
      this.callable = callable;
    }

   /**
    * Gets the ChartData.
    *
    * @return The JsonObjectBuilder.
    */
    @Override
    protected JsonObjectBuilder.JsonObject getChartData() throws Exception {
      JsonObjectBuilder valuesBuilder = new JsonObjectBuilder();
      Map<String, Integer> map = this.callable.call();
      if (map == null || map.isEmpty()) {
        return null;
      }
      for (Map.Entry<String, Integer> entry : map.entrySet()) {
        valuesBuilder.appendField(entry.getKey(), new int[] {entry.getValue()});
      }
      return new JsonObjectBuilder().appendField("values", valuesBuilder.build()).build();
    }
  }

 /**
  * MultiLineChart Data Handling.
  */
  public static class MultiLineChart extends CustomChart {

    private final Callable<Map<String, Integer>> callable;

   /**
    * Class constructor.
    *
    * @param chartId The id of the chart.
    * @param callable The callable which is used to request the chart data.
    */
    public MultiLineChart(final String chartId, final Callable<Map<String, Integer>> callable) {
      super(chartId);
      this.callable = callable;
    }

   /**
    * Gets the ChartData.
    *
    * @return The JsonObjectBuilder.
    */
    @Override
    protected JsonObjectBuilder.JsonObject getChartData() throws Exception {
      JsonObjectBuilder valuesBuilder = new JsonObjectBuilder();
      Map<String, Integer> map = this.callable.call();
      if (map == null || map.isEmpty()) {
        return null;
      }
      boolean allSkipped = true;
      for (Map.Entry<String, Integer> entry : map.entrySet()) {
        if (entry.getValue() == 0) {
          continue;
        }
        allSkipped = false;
        valuesBuilder.appendField(entry.getKey(), entry.getValue());
      }
      if (allSkipped) {
        return null;
      }
      return new JsonObjectBuilder().appendField("values", valuesBuilder.build()).build();
    }
  }

 /**
  * AdvancedPie Data Handling.
  */
  public static class AdvancedPie extends CustomChart {

    private final Callable<Map<String, Integer>> callable;

   /**
    * Class constructor.
    *
    * @param chartId The id of the chart.
    * @param callable The callable which is used to request the chart data.
    */
    public AdvancedPie(final String chartId, final Callable<Map<String, Integer>> callable) {
      super(chartId);
      this.callable = callable;
    }

   /**
    * Gets the ChartData.
    *
    * @return The JsonObjectBuilder.
    */
    @Override
    protected JsonObjectBuilder.JsonObject getChartData() throws Exception {
      JsonObjectBuilder valuesBuilder = new JsonObjectBuilder();
      Map<String, Integer> map = this.callable.call();
      if (map == null || map.isEmpty()) {
        return null;
      }
      boolean allSkipped = true;
      for (Map.Entry<String, Integer> entry : map.entrySet()) {
        if (entry.getValue() == 0) {
          continue;
        }
        allSkipped = false;
        valuesBuilder.appendField(entry.getKey(), entry.getValue());
      }
      if (allSkipped) {
        return null;
      }
      return new JsonObjectBuilder().appendField("values", valuesBuilder.build()).build();
    }
  }

 /**
  * CustomChart Data Handling.
  */
  public abstract static class CustomChart {

    private final String chartId;

   /**
    * Class constructor.
    *
    * @param chartId The id of the chart.
    */
    protected CustomChart(final String chartId) {
      if (chartId == null) {
        throw new IllegalArgumentException("chartId must not be null");
      }
      this.chartId = chartId;
    }

   /**
    * Gets the Requested JsonObject.
    *
    * @return The JsonObjectBuilder.
    */
    public JsonObjectBuilder.JsonObject getRequestJsonObject(
      BiConsumer<String, Throwable> errorLogger, boolean logErrors) {
      JsonObjectBuilder builder = new JsonObjectBuilder();
      builder.appendField("chartId", this.chartId);
      try {
        JsonObjectBuilder.JsonObject data = getChartData();
        if (data == null) {
          return null;
        }
        builder.appendField("data", data);
      } catch (Throwable t) {
        if (logErrors) {
          errorLogger.accept("Failed to get data for custom chart with id " + this.chartId, t);
        }
        return null;
      }
      return builder.build();
    }

    protected abstract JsonObjectBuilder.JsonObject getChartData() throws Exception;
  }

 /**
  * SingleLineChart Data Handling.
  */
  public static class SingleLineChart extends CustomChart {

    private final Callable<Integer> callable;

   /**
    * Class constructor.
    *
    * @param chartId The id of the chart.
    * @param callable The callable which is used to request the chart data.
    */
    public SingleLineChart(final String chartId, final Callable<Integer> callable) {
      super(chartId);
      this.callable = callable;
    }

   /**
    * Gets the ChartData.
    *
    * @return The JsonObjectBuilder.
    */
    @Override
    protected JsonObjectBuilder.JsonObject getChartData() throws Exception {
      int value = this.callable.call();
      if (value == 0) {
        return null;
      }
      return new JsonObjectBuilder().appendField("value", value).build();
    }
  }

 /**
  * SimplePie Data Handling.
  */
  public static class SimplePie extends CustomChart {

    private final Callable<String> callable;

   /**
    * Class constructor.
    *
    * @param chartId The id of the chart.
    * @param callable The callable which is used to request the chart data.
    */
    public SimplePie(final String chartId, final Callable<String> callable) {
      super(chartId);
      this.callable = callable;
    }

   /**
    * Gets the ChartData.
    *
    * @return The JsonObjectBuilder.
    */
    @Override
    protected JsonObjectBuilder.JsonObject getChartData() throws Exception {
      String value = this.callable.call();
      if (value == null || value.isEmpty()) {
        return null;
      }
      return new JsonObjectBuilder().appendField("value", value).build();
    }
  }
  
 /**
  * DrilldownPie Data Handling.
  */
  public static class DrilldownPie extends CustomChart {

    private final Callable<Map<String, Map<String, Integer>>> callable;

   /**
    * Class constructor.
    *
    * @param chartId The id of the chart.
    * @param callable The callable which is used to request the chart data.
    */
    public DrilldownPie(final String chartId, final Callable<Map<String, Map<String, Integer>>> callable) {
      super(chartId);
      this.callable = callable;
    }

   /**
    * Gets the ChartData.
    *
    * @return The JsonObjectBuilder.
    */
    @Override
    public JsonObjectBuilder.JsonObject getChartData() throws Exception {
      JsonObjectBuilder valuesBuilder = new JsonObjectBuilder();
      Map<String, Map<String, Integer>> map = this.callable.call();
      if (map == null || map.isEmpty()) {
        return null;
      }
      boolean reallyAllSkipped = true;
      for (Map.Entry<String, Map<String, Integer>> entryValues : map.entrySet()) {
        JsonObjectBuilder valueBuilder = new JsonObjectBuilder();
        boolean allSkipped = true;
        for (Map.Entry<String, Integer> valueEntry : map.get(entryValues.getKey()).entrySet()) {
          valueBuilder.appendField(valueEntry.getKey(), valueEntry.getValue());
          allSkipped = false;
        }
        if (!allSkipped) {
          reallyAllSkipped = false;
          valuesBuilder.appendField(entryValues.getKey(), valueBuilder.build());
        }
      }
      if (reallyAllSkipped) {
        return null;
      }
      return new JsonObjectBuilder().appendField("values", valuesBuilder.build()).build();
    }
  }

 /**
  * An extremely simple JSON builder.
  *
  * <p>While this class is neither feature-rich nor the most performant one, it's sufficient enough
  * for its use-case.
  */
  public static class JsonObjectBuilder {

    private StringBuilder builder = new StringBuilder();

    private boolean hasAtLeastOneField = false;

   /**
    * Class constructor.
    * 
    */
    public JsonObjectBuilder() {
      this.builder.append("{");
    }

   /**
    * Appends a null field to the JSON.
    *
    * @param key The key of the field.
    * @return A reference to this object.
    */
    public JsonObjectBuilder appendNull(final String key) {
      this.appendFieldUnescaped(key, "null");
      return this;
    }

   /**
    * Appends a string field to the JSON.
    *
    * @param key The key of the field.
    * @param value The value of the field.
    * @return A reference to this object.
    */
    public JsonObjectBuilder appendField(final String key, final String value) {
      if (value == null) {
        throw new IllegalArgumentException("JSON value must not be null");
      }
      this.appendFieldUnescaped(key, "\"" + escape(value) + "\"");
      return this;
    }

   /**
    * Appends an integer field to the JSON.
    *
    * @param key The key of the field.
    * @param value The value of the field.
    * @return A reference to this object.
    */
    public JsonObjectBuilder appendField(final String key, final int value) {
      this.appendFieldUnescaped(key, String.valueOf(value));
      return this;
    }

   /**
    * Appends an object to the JSON.
    *
    * @param key The key of the field.
    * @param object The object.
    * @return A reference to this object.
    */
    public JsonObjectBuilder appendField(final String key, final JsonObject object) {
      if (object == null) {
        throw new IllegalArgumentException("JSON object must not be null");
      }
      this.appendFieldUnescaped(key, object.toString());
      return this;
    }

   /**
    * Appends a string array to the JSON.
    *
    * @param key The key of the field.
    * @param values The string array.
    * @return A reference to this object.
    */
    public JsonObjectBuilder appendField(final String key, final String[] values) {
      if (values == null) {
        throw new IllegalArgumentException("JSON values must not be null");
      }
      String escapedValues =
          Arrays.stream(values)
              .map(value -> "\"" + escape(value) + "\"")
              .collect(Collectors.joining(","));
      this.appendFieldUnescaped(key, "[" + escapedValues + "]");
      return this;
    }

   /**
    * Appends an integer array to the JSON.
    *
    * @param key The key of the field.
    * @param values The integer array.
    * @return A reference to this object.
    */
    public JsonObjectBuilder appendField(final String key, final int[] values) {
      if (values == null) {
        throw new IllegalArgumentException("JSON values must not be null");
      }
      String escapedValues =
          Arrays.stream(values).mapToObj(String::valueOf).collect(Collectors.joining(","));
      this.appendFieldUnescaped(key, "[" + escapedValues + "]");
      return this;
    }

   /**
    * Appends an object array to the JSON.
    *
    * @param key The key of the field.
    * @param values The integer array.
    * @return A reference to this object.
    */
    public JsonObjectBuilder appendField(final String key, final JsonObject[] values) {
      if (values == null) {
        throw new IllegalArgumentException("JSON values must not be null");
      }
      String escapedValues =
          Arrays.stream(values).map(JsonObject::toString).collect(Collectors.joining(","));
      this.appendFieldUnescaped(key, "[" + escapedValues + "]");
      return this;
    }

   /**
    * Appends a field to the object.
    *
    * @param key The key of the field.
    * @param escapedValue The escaped value of the field.
    */
    private void appendFieldUnescaped(final String key, final String escapedValue) {
      if (this.builder == null) {
        throw new IllegalStateException("JSON has already been built");
      }
      if (key == null) {
        throw new IllegalArgumentException("JSON key must not be null");
      }
      if (this.hasAtLeastOneField) {
        this.builder.append(",");
      }
      this.builder.append("\"").append(escape(key)).append("\":").append(escapedValue);
      this.hasAtLeastOneField = true;
    }

   /**
    * Builds the JSON string and invalidates this builder.
    *
    * @return The built JSON string.
    */
    public JsonObject build() {
      if (this.builder == null) {
        throw new IllegalStateException("JSON has already been built");
      }
      JsonObject object = new JsonObject(this.builder.append("}").toString());
      this.builder = null;
      return object;
    }

   /**
    * Escapes the given string like stated in https://www.ietf.org/rfc/rfc4627.txt.
    *
    * <p>This method escapes only the necessary characters '"', '\'. and '\u0000' - '\u001F'.
    * Compact escapes are not used (e.g., '\n' is escaped as "\u000a" and not as "\n").
    *
    * @param value The value to escape.
    * @return The escaped value.
    */
    private static String escape(final String value) {
      final StringBuilder builder = new StringBuilder();
      for (int i = 0; i < value.length(); i++) {
        char c = value.charAt(i);
        if (c == '"') {
          builder.append("\\\"");
        } else if (c == '\\') {
          builder.append("\\\\");
        } else if (c <= '\u000F') {
          builder.append("\\u000").append(Integer.toHexString(c));
        } else if (c <= '\u001F') {
          builder.append("\\u00").append(Integer.toHexString(c));
        } else {
          builder.append(c);
        }
      }
      return builder.toString();
    }

   /**
    * A super simple representation of a JSON object.
    *
    * <p>This class only exists to make methods of the {@link JsonObjectBuilder} type-safe and not
    * allow a raw string inputs for methods like {@link JsonObjectBuilder#appendField(String,
    * JsonObject)}.
    */
    public static class JsonObject {

      private final String value;

     /**
      * Class constructor.
      *
      * @param value - The value of the JsonObject.
      */
      private JsonObject(final String value) {
        this.value = value;
      }

     /**
      * Returns the value as a String.
      * @return The value converted to a String.
      */
      @Override
      public String toString() {
        return this.value;
      }
    }
  }
}