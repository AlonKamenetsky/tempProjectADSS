package SuppliersModule.DomainLayer;

public class ContactInfo {
    String phoneNumber;
    String address;
    String email;
    String name;
    public ContactInfo(String phoneNumber, String address, String email, String name) {
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.email = email;
        this.name = name;;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    @Override
    public String toString() {
        return phoneNumber + "\t" + address + "\t" + email + "\t" + name + "\t";
    }
}
