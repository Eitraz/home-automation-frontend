package com.eitraz.automation;

import com.byteowls.vaadin.chartjs.ChartJs;
import com.byteowls.vaadin.chartjs.config.LineChartConfig;
import com.byteowls.vaadin.chartjs.data.Dataset;
import com.byteowls.vaadin.chartjs.data.LineDataset;
import com.byteowls.vaadin.chartjs.options.Position;
import com.byteowls.vaadin.chartjs.options.scale.Axis;
import com.byteowls.vaadin.chartjs.options.scale.CategoryScale;
import com.byteowls.vaadin.chartjs.options.scale.LinearScale;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.fluent.Request;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TemperaturesView extends VerticalLayout {
    private static final String BASE_URL = "http://192.168.1.30:8080/temperatures/";

    private Map<String, TemperatureComponent> temperatureComponents = new HashMap<>();
    private UpdateTemperaturesThread updateTemperaturesThread;

    public TemperaturesView() {
        setSizeFull();
        setMargin(true);
        setSpacing(false);

        addComponent(createTemperatures());
        //addComponent(createGraph());

        updateTemperaturesThread = new UpdateTemperaturesThread();
        updateTemperaturesThread.start();
    }

//    private Component createGraph() {
//        List<Map<String, Object>> history = getTemperatureHistory().get("BalconyTempSensor");
//        List<Double> historyData = history.stream()
//                                          .map(data -> Double.valueOf(String.valueOf(data.get("temperature"))))
//                                          .collect(Collectors.toList());
//
//        Double max = historyData.stream().max(Double::compareTo).orElse(25d);
//        Double min = historyData.stream().min(Double::compareTo).orElse(-15d);
//
//        LineChartConfig lineConfig = new LineChartConfig();
//        lineConfig.data()
//                  .labelsAsList(history.stream().map(data -> ".").collect(Collectors.toList()))
//                  //.labels("January", "February", "March", "April", "May", "June", "July")
//                  .addDataset(new LineDataset()
//                          .label("test")
//                          .borderColor("rgb(255,255,255)")
//                          .fill(true)
//                          .pointRadius(0))
//                  .and()
//
//                  .options()
//                  .responsive(false)
//
//                  .title()
//                  .display(false)
//                  .and()
//
//                  //.legend()
//                  //.display(false)
//                  //.and()
//
//                  .scales()
//                      .add(Axis.X, new CategoryScale()
//                              .display(false)
//                      )
//                      .add(Axis.Y, new LinearScale()
//                              .display(true)
//                              .scaleLabel()
//                              .and()
//                              .position(Position.RIGHT))
//                  .and()
//
//                  .done();
//
//        Dataset<String, Double> dataset = (Dataset<String, Double>) lineConfig.data().getFirstDataset();
//        dataset.dataAsList(historyData);
//
//        ChartJs chart = new ChartJs(lineConfig);
//        //chart.setSizeFull();
//        chart.setWidth(800, Unit.PIXELS);
//        chart.setHeight(300, Unit.PIXELS);
//        return chart;
//    }

    @Override
    public void detach() {
        super.detach();

        updateTemperaturesThread = null;
    }

    private Component createTemperatures() {
        GridLayout gridLayout = new GridLayout(3, 1);
        gridLayout.setSizeFull();
        gridLayout.setSpacing(true);
        gridLayout.setMargin(false);

        Map<String, List<Map<String, Object>>> history = new HashMap<>();//getTemperatureHistory();

        getTemperatures().forEach((key, values) -> {
            Panel panel = new Panel();
            panel.addStyleName(ValoTheme.PANEL_WELL);
            panel.setSizeFull();

            TemperatureComponent temperatureComponent = new TemperatureComponent(
                    getTitle(key),
                    String.valueOf(values.get("temperature")),
                    String.valueOf(values.get("time")),
                    history.get(key));

            temperatureComponents.put(key, temperatureComponent);

            panel.setContent(temperatureComponent);
            gridLayout.addComponent(panel);
        });

        return gridLayout;
    }

    private String getTitle(String key) {
        String title = key.replace("TempSensor", "");
        title = StringUtils.join(StringUtils.splitByCharacterTypeCamelCase(title), " ");
        return title;
    }

    private class TemperatureComponent extends VerticalLayout {
        private final Label temperatureLabel;
        private final Label timeLabel;

        TemperatureComponent(String title, String temperature, String time, List<Map<String, Object>> history) {
            setSizeFull();

            VerticalLayout labelContainer = new VerticalLayout();
            labelContainer.setSizeFull();
            labelContainer.setMargin(false);
            labelContainer.setSpacing(false);

            // Title
            Label titleLabel = new Label(title);
            titleLabel.addStyleName(ValoTheme.LABEL_SMALL);
            labelContainer.addComponent(titleLabel);
            labelContainer.setComponentAlignment(titleLabel, Alignment.MIDDLE_CENTER);

            // Temperature
            temperatureLabel = new Label(temperature);
            temperatureLabel.addStyleName(ValoTheme.LABEL_BOLD);
            temperatureLabel.addStyleName(ValoTheme.LABEL_HUGE);
            labelContainer.addComponent(temperatureLabel);
            labelContainer.setComponentAlignment(temperatureLabel, Alignment.MIDDLE_CENTER);

            // Time
            timeLabel = new Label(time);
            timeLabel.addStyleName(ValoTheme.LABEL_TINY);
            labelContainer.addComponent(timeLabel);
            labelContainer.setComponentAlignment(timeLabel, Alignment.MIDDLE_CENTER);


//            List<Double> historyData = history.stream()
//                                              .map(data -> Double.valueOf(String.valueOf(data.get("temperature"))))
//                                              .collect(Collectors.toList());
//
//            Double max = historyData.stream().max(Double::compareTo).orElse(25d);
//            Double min = historyData.stream().min(Double::compareTo).orElse(-15d);
//
//            LineChartConfig lineConfig = new LineChartConfig();
//            lineConfig.data()
//                      .labelsAsList(history.stream().map(data -> ".").collect(Collectors.toList()))
//                      //.labels("January", "February", "March", "April", "May", "June", "July")
//                      .addDataset(new LineDataset()
//                              .dataAsList(historyData)
//                              .label("test")
//                              .borderColor("rgb(255,255,255)")
//                              .fill(true)
//                              .pointRadius(0))
//                      .and()
//
//                      .options()
//                      .responsive(false)
//
//                      .title()
//                      .display(false)
//                      .and()
//
//                      .legend()
//                      .display(false)
//                      .and()
//
//                      .scales()
//                      .add(Axis.X, new CategoryScale()
//                              .display(false)
//                      )
//                      .add(Axis.Y, new LinearScale()
//                              .display(true)
//                              .scaleLabel()
//                              .and()
//                              .position(Position.RIGHT))
//                      .and()
//
//                      .done();
//
//            ChartJs chart = new ChartJs(lineConfig);
//            //chart.setWidth(350, Unit.PIXELS);
//            //chart.setHeight(60, Unit.PIXELS);
//            chart.setWidth(100, Unit.PERCENTAGE);
//            chart.setHeight(100, Unit.PERCENTAGE);
//            //addComponent(chart);
//
//            VerticalLayout chartContainer = new VerticalLayout();
//            chartContainer.setMargin(false);
//            chartContainer.setSpacing(false);
//            chartContainer.setSizeFull();
//            chartContainer.addComponent(chart);

            addComponent(labelContainer);
            //setComponentAlignment(labelContainer, Alignment.TOP_CENTER);

            //addComponent(chartContainer);
            //setComponentAlignment(chartContainer, Alignment.BOTTOM_CENTER);
        }

        public void updateTemperature(String temperature, String time) {
            temperatureLabel.setValue(temperature);
            timeLabel.setValue(time);
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Map<String, Object>> getTemperatures() {
        try {
            String responseString = Request
                    .Get(BASE_URL).execute()
                    .returnContent().asString(StandardCharsets.UTF_8);

            return new ObjectMapper().readValue(responseString, Map.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, List<Map<String, Object>>> getTemperatureHistory() {
        try {
            String responseString = Request
                    .Get(BASE_URL + "/history/24h").execute()
                    .returnContent().asString(StandardCharsets.UTF_8);

            return new ObjectMapper().readValue(responseString, Map.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void updateTemperatures() {
        Map<String, Map<String, Object>> temperatures = getTemperatures();

        UI ui = getUI();
        if (ui == null) {
            updateTemperaturesThread = null;
            return;
        }

        ui.access(() -> temperatures.forEach((key, values) -> {
            TemperatureComponent temperatureComponent = temperatureComponents.get(key);
            if (temperatureComponent != null) {
                temperatureComponent.updateTemperature(
                        String.valueOf(values.get("temperature")),
                        String.valueOf(values.get("time")));
            }
        }));
    }

    private class UpdateTemperaturesThread extends Thread {
        @Override
        public void run() {
            try {
                Thread.sleep(30000);

                while (updateTemperaturesThread == this) {
                    updateTemperatures();
                    Thread.sleep(30000);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
