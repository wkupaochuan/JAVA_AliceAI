/*
 * Copyleft (C) 2005 Hélio Perroni Filho xperroni@yahoo.com ICQ: 2490863 This
 * file is part of ChatterBean. ChatterBean is free software; you can
 * redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version. ChatterBean is distributed in
 * the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU General Public License for more details. You should have received a
 * copy of the GNU General Public License along with ChatterBean (look at the
 * Documents/ directory); if not, either write to the Free Software Foundation,
 * Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA, or visit
 * (http://www.gnu.org/licenses/gpl.txt).
 */

package bitoflife.chatterbean;

import static bitoflife.chatterbean.text.Sentence.ASTERISK;

import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import bitoflife.chatterbean.script.BeanshellInterpreter;
import bitoflife.chatterbean.text.Request;
import bitoflife.chatterbean.text.Response;
import bitoflife.chatterbean.text.Sentence;
import bitoflife.chatterbean.text.Transformations;

/**
 * A conversational context. This class stores information such as the history
 * of a conversation and predicate values, which the Alice Bot can refer to
 * while responding user requests.
 */
public class Context {

	/*
	 * Attribute Section
	 */

	/** Map of property change listeners. */
	// private final Map<String, ContextPropertyChangeListener> listeners = new
	// HashMap<String, ContextPropertyChangeListener>();
	private final Map<String, Map<String, ContextPropertyChangeListener>> listeners = new HashMap<String, Map<String, ContextPropertyChangeListener>>();

	private OutputStream output;

	/** Map of context properties. */
	// private final Map<String, Object> properties = new HashMap<String,
	// Object>();
	private final Map<String, Map<String, Object>> properties = new HashMap<String, Map<String, Object>>();

	private final Random random = new Random();

	// private final List<Request> requests = new LinkedList<Request>();
	private final Map<String, List<Request>> requests = new HashMap<String, List<Request>>();

	// private final List<Response> responses = new LinkedList<Response>();
	private final Map<String, List<Response>> responses = new HashMap<String, List<Response>>();

	private final long seed = 0;

	// private Sentence that;
	private Map<String, Sentence> that = new HashMap<String, Sentence>();

	// private Sentence topic;
	private Map<String, Sentence> topic = new HashMap<String, Sentence>();

	/** Set of normalizing transformations applied to unstructured text. */
	private Transformations transformations;

	/*
	 * Constructor Section
	 */

	/**
	 * Default Constructor.
	 */
	public Context() {
		property("beanshell.interpreter", new BeanshellInterpreter(), "-100");

		addContextPropertyChangeListener(new ContextRandomSeedChangeListener());
		addContextPropertyChangeListener(new ContextTopicChangeListener());
	}

	/**
	 * Creates a new Context object with the given set of normalizing
	 * transformations.
	 * 
	 * @param transformations
	 *            A set of normalizing transformations.
	 */
	public Context(Transformations transformations) {
		this();
		this.transformations = transformations;
	}

	/*
	 * Event Section
	 */

	/**
	 * Adds a property change listener to this context object.
	 * 
	 * @param listener
	 *            A property change listener. If there already is a listener
	 *            with the same name of this one, the old listener will be
	 *            discarded.
	 */
	public void addContextPropertyChangeListener(
			ContextPropertyChangeListener listener) {
		Map<String, ContextPropertyChangeListener> tmp = new HashMap<String, ContextPropertyChangeListener>();
		tmp.put(listener.name(), listener);
		listeners.put("-100", tmp);
	}

	public void appendRequest(Request request, String userId) {
		if (requests.get(userId) == null) {
			List<Request> list = new LinkedList<Request>();
			list.add(0, request);
			requests.put(userId, list);
		} else {
			requests.get(userId).add(0, request);
			if (requests.get(userId).size() >= 20) {
				requests.get(userId).remove(requests.get(userId).size() - 1);
			}
		}
	}

	/*
	 * Method Section
	 */

	public void appendResponse(Response response, String userId) {
		transformations.normalization(response);
		if (responses.get(userId) == null) {
			List<Response> list = new LinkedList<Response>();
			list.add(0, response);
			responses.put(userId, list);
		} else {
			responses.get(userId).add(0, response);
			if (responses.get(userId).size() >= 20) {
				responses.get(userId).remove(responses.get(userId).size() - 1);
			}
		}

		that.put(userId, response.lastSentence(0));
		transformations.normalization(that.get(userId));
	}

	public Request getRequests(int index, String userId) {
		return requests.get(userId).get(index);
	}

	public Response getResponses(int index, String userId) {
		return responses.get(userId).get(index);
	}

	/*
	 * Accessor Section
	 */

	public Sentence getThat(String userId) {
		if (that.get(userId) == null) {
			return ASTERISK;
		}
		return that.get(userId);
	}

	public Sentence getTopic(String userId) {
		if (topic.get(userId) == null) {
			return ASTERISK;
		}
		return topic.get(userId);
	}

	public Transformations getTransformations() {
		return transformations;
	}

	public String id() {
		String id = (String) property("bot.id", "-100");
		if ("".equals(id)) {
			return Integer.toString(hashCode());
		} else {
			return id;
		}
	}

	public OutputStream outputStream() throws IOException {
		if (output == null) {
			String path = (String) property("bot.output", "-100");
			System.out.println(path);
			File file = new File(path);
			if (file.isDirectory()) {
				path = file.getPath() + "/gossip-" + id() + ".txt";
			}

			outputStream(new FileOutputStream(path));
		}

		return output;
	}

	public void outputStream(OutputStream output) {
		this.output = output;
	}

	public void print(String output) throws IOException {
		outputStream().write(output.getBytes());
		outputStream().write('\n');
	}

	/*
	 * Property Section
	 */

	public void property(String name, Object value, String userId) {
		if (userId == null || "".equals(userId)) {
			userId = "-100";
		}

		if (listeners.get(userId) != null) {
			ContextPropertyChangeListener listener = listeners.get(userId).get(
					name);
			if (listener != null) {
				Object oldValue = properties.get(userId).get(name);
				PropertyChangeEvent event = new PropertyChangeEvent(this, name,
						oldValue, value);
				listener.propertyChange(event);
			}
		}

		if (properties.get(userId) == null) {
			Map<String, Object> tmp = new HashMap<String, Object>();
			tmp.put(name, value);
			properties.put(userId, tmp);
		} else {
			properties.get(userId).put(name, value);
		}
	}

	public Object property(String name, String userId) {
		if (userId == null || "".equals(userId)) {
			userId = "-100";
		}

		if (properties.get(userId) == null) {
			return null;
		} else {
			return properties.get(userId).get(name);
		}
	}

	public Random random() {
		return random;
	}

	/**
	 * Sets the value of the seed used by the internal random number generator.
	 * 
	 * @param seed
	 *            The seed used by the internal random number generator.
	 */
	public void random(long seed) {
		random.setSeed(seed);
	}

	/**
	 * Removes a property change listener to this context object. Although
	 * listeners are stored by name, for the removing to actually occur it is
	 * not enough to simply pass a listener with the same name; the same
	 * <i>object</i> must be passed, otherwise this method does nothing.
	 * 
	 * @param listener
	 *            A property change listener.
	 */
	public void removeContextPropertyChangeListener(
			ContextPropertyChangeListener listener) {
		ContextPropertyChangeListener listening = listeners.get("-100").get(
				listener.name());
		if (listening == listener) {
			listeners.remove(listener.name());
		}
	}

	public void setTopic(Sentence topic) {
		// if (topic == null) {
		// this.topic = ASTERISK;
		// }
		// this.topic = topic;
	}

	public void setTransformations(Transformations transformations) {
		this.transformations = transformations;
	}
}
