package org.vaadin.example.ui;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.vaadin.example.domain.AppUser;
import org.vaadin.example.security.SecurityService;

public class MainLayout extends AppLayout {

    private final SecurityService securityService;

    public MainLayout(SecurityService securityService) {
        this.securityService = securityService;

        if (securityService.isLoggedIn()) {
            createHeader();
            createDrawer();
        } else {
            UI.getCurrent().navigate(LoginView.class);
        }
    }

    private void createHeader() {
        H1 appName = new H1("Request Management System");
        appName.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);

        AppUser appUser = securityService.getAuthenticatedUser();

        if (appUser == null) {
            return;
        }

        Avatar userAvatar = new Avatar(appUser.getName());
        MenuBar userMenu = createUserMenu();

        HorizontalLayout header = new HorizontalLayout(
                new DrawerToggle(),
                appName,
                new HorizontalLayout(userAvatar, userMenu)
        );
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.expand(appName);
        header.setWidthFull();
        header.addClassNames(LumoUtility.Padding.Vertical.NONE, LumoUtility.Padding.Horizontal.MEDIUM);

        addToNavbar(header);
    }

    private MenuBar createUserMenu() {
        MenuBar menu = new MenuBar();
        MenuItem profile = menu.addItem("Профиль");
        profile.addClickListener(e -> showProfile());

        menu.addItem("Выход", e -> {
            securityService.logout();
            UI.getCurrent().navigate(LoginView.class);
        });
        return menu;
    }

    private void createDrawer() {
        RouterLink requestsLink = new RouterLink("Заявки", RequestsView.class);
        addToDrawer(new VerticalLayout(requestsLink));
    }

    private void showProfile() {
        AppUser appUser = securityService.getAuthenticatedUser();
        if (appUser != null) {
            Notification.show("Пользователь: " + appUser.getName(), 3000, Notification.Position.TOP_CENTER);
        }
    }
}