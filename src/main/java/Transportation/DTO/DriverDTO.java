package Transportation.DTO;

import java.util.List;

public record DriverDTO(
    String driverId,
    String  driverName,
    List<String> licenseTypes,
    boolean isAvailable
) {}