/**
 *
 */
package org.cerumen;

import java.awt.Point;
import java.lang.reflect.Method;
import java.util.concurrent.Future;

import jrox.jabsorb.client.xmpp.XMPPSessionFactory;
import jrox.jabsorb.serializer.impl.PointSerializer;

import org.jabsorb.client.Session;
import org.jabsorb.client.TransportRegistry;
import org.jabsorb.client.async.AsyncClient;
import org.jabsorb.client.async.AsyncProxy;
import org.jabsorb.client.async.AsyncResultCallback;
import org.jabsorb.client.async.AsyncSessionUtil;

/**
 * @author matthijs
 *
 */
public class ClientTest {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(final String[] args) throws Exception {
		final TransportRegistry registry = new TransportRegistry();
		XMPPSessionFactory.register(registry);

		final Session session = registry.createSession("xmpp://test1:test1@localhost:5222/room1@conference.localhost?join");

		// synchronous
		//final Client client = new Client(session);

		// asynchronous
		final AsyncClient client = new AsyncClient(AsyncSessionUtil.toAsyncSession(session));

		client.getSerializer().registerSerializer(new PointSerializer());

		final Level level = (Level) client.openProxy("level", Level.class);

		// 1. okay if we don't need the result
		//level.movePiece((byte) 1, new Point(0, 0), new Point(1, 0));

		if (level instanceof AsyncProxy) {
			// 2. get a future result
//			synchronized (level) {
//				level.movePiece((byte) 1, new Point(0, 0), new Point(1, 0));
//				final Future<Object> futureResult = ((AsyncProxy)level).getFutureResult();
//				final Object result = futureResult.get();
//
//				System.out.println("Result: " + result);
//			}

			// 3. register a callback
//			synchronized (level) {
//				((AsyncProxy)level).setResultCallback(new AsyncResultCallback<Object, Object, Method>() {
//					@Override
//					public void onAsyncResult(final Object source, final Future<Object> result, final Method context) {
//						try {
//							System.out.println("Result: " + result.get());
//						} catch (final Exception e) {
//							// make it a runtime exception
//							throw new RuntimeException(e);
//						}
//					}
//				});
//				level.movePiece((byte) 1, new Point(0, 0), new Point(1, 0));
//
//				// unset it, lest we get callbacks from other invocations as well
//				((AsyncProxy)level).setResultCallback(null);
//			}
//
//			// sleep a while before exiting to allow callback to be called
//			Thread.sleep(3000);

			// 4. Or do both!
			synchronized (level) {
				((AsyncProxy)level).setResultCallback(new AsyncResultCallback<Object, Object, Method>() {
					@Override
					public void onAsyncResult(final Object source, final Future<Object> futureResult, final Method context) {
						try {
							System.out.println("Result from callback: " + futureResult.get());
						} catch (final Exception e) {
							// TODO: seperate logic for InterruptedException and ExecutionException

							// make it a runtime exception for now
							throw new RuntimeException(e);
						}
					}
				});
				level.movePiece((byte) 1, new Point(0, 0), new Point(1, 0));

				// unset it, lest we get callbacks from other invocations as well
				((AsyncProxy)level).setResultCallback(null);

				final Future<Object> futureResult = ((AsyncProxy)level).getFutureResult();
				final Object result = futureResult.get();

				System.out.println("Result from future: " + result);
			}
			// sleep a while before exiting to allow callback to be called
			Thread.sleep(5000);
		}
	}

}
