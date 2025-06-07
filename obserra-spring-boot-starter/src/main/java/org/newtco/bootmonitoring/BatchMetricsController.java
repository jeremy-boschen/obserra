package org.newtco.bootmonitoring;

import java.util.List;

import org.springframework.boot.actuate.metrics.MetricsEndpoint;
import org.springframework.boot.actuate.metrics.MetricsEndpoint.MetricDescriptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ConditionalOnProperty(prefix = "obserra", name = "enabled", havingValue = "true")
public class BatchMetricsController {

  private final MetricsEndpoint metrics;

  public BatchMetricsController(MetricsEndpoint metrics) {
    this.metrics = metrics;
  }

  @GetMapping("/actuator/metrics/batch")
  public List<MetricDescriptor> allMetrics() {
    return metrics.listNames().getNames().stream()
        .map(name -> metrics.metric(name, null))
        .toList();
  }
}
