package Transportation.Presentation;

import Transportation.Presentation.cli.TransportationMenu;
import Transportation.SystemInitializer.SystemInitializer;

public class TransportationMain {
    public static void main(String[] args) {
        TransportationMenu menu = SystemInitializer.buildApplication();
        menu.show();
    }
}