package org.cerumen;

import java.awt.BorderLayout;

import javax.swing.JFrame;

import org.jabsorb.JSONRPCBridge;
import org.jabsorb.JSONRPCXMPPConnection;
import org.jabsorb.serializer.impl.PointSerializer;
import org.jivesoftware.smack.Roster.SubscriptionMode;

/**
 * Hello world!
 *
 */
public class App extends JFrame {
	private static final long serialVersionUID = 1L;

	public App(final Level level) throws Exception {
		final Game game = new Game(level);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		add(game, BorderLayout.CENTER);
		pack();
		setResizable(false);
		setVisible(true);
	}

    public static void main(final String[] args) throws Exception {
    	// TODO: commons-cli
    	// TODO: combine local level and remote level
		final Level level = LocalLevel.loadLevel(args[0]);

		// initialize json-rpc over xmpp
		JSONRPCBridge.getGlobalBridge().getSerializer().registerSerializer(new PointSerializer());
		JSONRPCBridge.getGlobalBridge().registerObject("level", level);

		final JSONRPCXMPPConnection connection = new JSONRPCXMPPConnection(args[1]);
		connection.getXmppConnection().getRoster().setSubscriptionMode(SubscriptionMode.accept_all);

    	new App(level);
    }
}
