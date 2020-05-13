package top.icss.mode;

import lombok.Data;

import java.util.List;

/**
 * @author cd
 * @desc
 * @create 2020/5/12 17:26
 * @since 1.0.0
 */
@Data
public class ServiceModel {
    private String serviceName;
    private String startTime;
    private List<ServiceProvider> serviceProviders;

}
