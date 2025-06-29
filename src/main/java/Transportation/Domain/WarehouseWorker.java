package Transportation.Domain;

public class WarehouseWorker {
    private final String hwId;
    private final String hwName;
    private boolean isAvailable;



    public WarehouseWorker(String hwId, String hwName) {
        this.hwId = hwId;
        this.hwName = hwName;
        this.isAvailable = true;
    }

    public String getHwId() {
        return hwId;
    }
    public String getHwName() {
        return hwName;
    }
    public boolean isAvailable() {
        return isAvailable;
    }
    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public String toString() {
        return String.format("WarehouseWorkerId: %s,\n WarehouseNameWorker: %s,\n Available: %s",
                hwId,hwName,isAvailable ? "Yes" : "No"
        );

    }

}
