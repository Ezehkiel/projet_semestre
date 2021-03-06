package ch.heigvd.pro.a03.commands.auth;

import ch.heigvd.pro.a03.menus.auth.AuthMenu;
import ch.heigvd.pro.a03.menus.auth.ConnectionMenu;
import ch.heigvd.pro.a03.users.User;
import ch.heigvd.pro.a03.server.HttpServerUtils;

/**
 * Logs in a player
 */
public class LoginCommand extends AuthCommand {

    private ConnectionMenu menu;

    /**
     * Creates a new player login command.
     * @param menu the connection menu
     * @param receiver the receiver
     */
    public LoginCommand(ConnectionMenu menu, AuthMenu receiver) {
        super(receiver);
        this.menu = menu;
    }

    @Override
    public void execute(Object... args) {

        getReceiver().clearError();

        checkPlayer(HttpServerUtils.login(menu.getUsername(), menu.getPassword()));
    }
}
