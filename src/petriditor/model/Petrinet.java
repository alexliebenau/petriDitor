package propra.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * This class represents a Petrinet and provides functionality for managing and manipulating it.
 * It includes methods for cloning, sorting places, retrieving and updating markings, and managing elements of the Petrinet.
 */
public class Petrinet implements Serializable {
	private static final long serialVersionUID = 8391892851609691127L;

	/**
	 * Place representing the intital Place of the Petrinet
	 */
	public Place InitialPlace;

	/**
	 * List storing the initial marking of the Petrinet
	 */
	public LinkedList<Integer> InitialMarking;

	/**
	 * HashMap ID -> Place containing all Places
	 */
	public LinkedHashMap<String, Place> Places;

	/**
	 * HashMap ID -> Transition containing all Transitions
	 */
	public LinkedHashMap<String, Transition> Transitions;

    /**
     * Default constructor. Initializes a new Petrinet with empty places and transitions.
     */
	public Petrinet() {
		this.Places = new LinkedHashMap<>();
		this.Transitions = new LinkedHashMap<>();
		this.InitialPlace = Places.get("p1");
		this.InitialMarking = new LinkedList<>();
	}

    /**
     * Constructor that initializes a Petrinet with a specific initial place.
     *
     * @param id The identifier of the initial place.
     */
	public Petrinet(final String id) {
		this.Places = new LinkedHashMap<>();
		this.Transitions = new LinkedHashMap<>();
		this.InitialPlace = Places.get(id);
		this.InitialMarking = new LinkedList<>();
	}

    /**
     * Creates a deep clone of this Petrinet instance.
     *
     * @return A new Petrinet object that is a clone of this instance.
     */
	@Override
	public Petrinet clone() {
		// return a clone using serialization
		try {
		    ByteArrayOutputStream baos = new ByteArrayOutputStream();
		    ObjectOutputStream out = new ObjectOutputStream(baos);
		    out.writeObject(this);
		    out.flush();
		    ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
		    ObjectInputStream in = new ObjectInputStream(bais);
		    return (Petrinet) in.readObject();
	    } catch (IOException | ClassNotFoundException e) {
	        e.printStackTrace();
	        return null;
	    }
	}

    /**
     * Sorts the places of the Petrinet in alphabetical order based on their identifiers.
     */
	public void sortPlaces() {
		// sort places alphabetically
		List<Map.Entry<String, Place>> entryList = new ArrayList<>(this.Places.entrySet());
		entryList.sort(Comparator.comparing(Map.Entry::getKey));
		LinkedHashMap<String, Place> sortedPlaces = new LinkedHashMap<>();
        for (Map.Entry<String, Place> entry : entryList) {
            sortedPlaces.put(entry.getKey(), entry.getValue());
        }
        this.Places = sortedPlaces;
	}

	/**
     * Retrieves the identifier of a given place.
     *
     * @param p The place whose identifier is to be retrieved.
     * @return The identifier of the place.
     * @throws NoSuchElementException If the place is not found.
     */
	public String getID(Place p) {
		for (Map.Entry<String, Place> entry : Places.entrySet()) {
			if (entry.getValue() == p) {
				return entry.getKey();
			}
		}
		throw new NoSuchElementException(String.format("Place with name %s not found.", p.Name));
	}

    /**
     * Retrieves the identifier of a given transition.
     *
     * @param t The transition whose identifier is to be retrieved.
     * @return The identifier of the transition.
     * @throws NoSuchElementException If the transition is not found.
     */
	public String getID(Transition t) {
		for (Map.Entry<String, Transition> entry : Transitions.entrySet()) {
			if (entry.getValue() == t) {
				return entry.getKey();
			}
		}
		throw new NoSuchElementException(String.format("Transition with name %s not found.", t.Name));
	}

    /**
     * Returns the current marking of the Petrinet.
     *
     * @return A LinkedList of Integers representing the current marking.
     */
	public LinkedList<Integer> getMarking() {
		// returns marking as linked list
		LinkedList<Integer> m = new LinkedList<>();
		for (String id : this.Places.keySet()) {
			Integer tokens = this.Places.get(id).Tokens;
			m.add(tokens);
		}
		return m;
	}

    /**
     * Returns the current marking of the Petrinet as a formatted string.
     *
     * @return A string representation of the current marking.
     */
	public String getMarkingString() {
		// returns marking as string
		LinkedList<Integer> markingList = this.getMarking();
        StringBuilder result = new StringBuilder("(");

        for (Integer number : markingList) {
            result.append(number).append("|");
        }
        // Remove the trailing '|' if the list isn't empty
        if (!markingList.isEmpty()) {
            result.deleteCharAt(result.length() - 1);
        }
        result.append(")");
        return result.toString();
    }


    /**
     * Updates the marking of the Petrinet with the provided marking.
     *
     * @param marking The new marking to be applied to the Petrinet.
     */
	public void updateMarking(final LinkedList<Integer> marking) {
		// applies marking (as linked list) to petrinet
		Integer elem;
		int index = 0;
		for (String id : this.Places.keySet()) {
			elem = marking.get(index);
			this.Places.get(id).Tokens = elem;
			index++;
		}
	}

    /**
     * Sets the name of a place or a transition based on its identifier.
     *
     * @param id   The identifier of the place or transition.
     * @param name The new name to be assigned.
     * @throws NoSuchElementException If no element with the given identifier is found.
     */
	public void setName(final String id, final String name) {
		if(this.Places.containsKey(id)) {
			Place p = this.Places.get(id);
			p.Name = name;
		}
		else if(this.Transitions.containsKey(id)) {
			Transition t = this.Transitions.get(id);
			t.Name = name;
		} else {
			throw new NoSuchElementException(String.format("No element with id %s found.", id));
		}
	}

    /**
     * Sets the position of a place or a transition based on its identifier.
     *
     * @param id       The identifier of the place or transition.
     * @param position The new position to be assigned as an array of Integers.
     * @throws NoSuchElementException If no element with the given identifier is found.
     */
	public void setPosition(final String id, final Integer[] position) {
		if(this.Places.containsKey(id)) {
			Place p = this.Places.get(id);
			p.Position = position;
		}
		else if(this.Transitions.containsKey(id)) {
			Transition t = this.Transitions.get(id);
			t.Position = position;
		} else {
			throw new NoSuchElementException(String.format("No element with id %s found.", id));
		}
	}

	// Overloaded addPlace & addTransition methods

    /**
     * Adds a new place to the Petrinet with a specified identifier and token count.
     *
     * @param id     The identifier for the new place.
     * @param tokens The initial token count for the place.
     */
	public void addPlace(final String id, final int tokens) {
		this.Places.put(id, new Place(tokens, "unnamed"));
	}

    /**
     * Adds a new place to the Petrinet with a specified identifier and name.
     *
     * @param id   The identifier for the new place.
     * @param name The name of the new place.
     */
	public void addPlace(final String id, final String name) {
		this.Places.put(id, new Place(0, name));
	}

    /**
     * Adds a new place to the Petrinet with a specified identifier, token count, and name.
     *
     * @param id     The identifier for the new place.
     * @param tokens The initial token count for the place.
     * @param name   The name of the new place.
     */
	public void addPlace(final String id, final int tokens, final String name) {
		this.Places.put(id, new Place(tokens, name));
	}

    /**
     * Adds a new transition to the Petrinet with a specified identifier.
     *
     * @param id The identifier for the new transition.
     */
	public void addTransition(final String id) {
		this.Transitions.put(id, new Transition("unnamed"));
	}

    /**
     * Adds a new transition to the Petrinet with a specified identifier and name.
     *
     * @param id   The identifier for the new transition.
     * @param name The name of the new transition.
     */
	public void addTransition(final String id, final String name) {
		this.Transitions.put(id, new Transition(name));
	}

    /**
     * Adds an arc between a place and a transition or vice versa in the Petrinet.
     *
     * @param id   The identifier of the arc.
     * @param pre  The identifier of the preceding element (place or transition).
     * @param post The identifier of the succeeding element (place or transition).
     * @throws NoSuchElementException If either the place or the transition is not found.
     */
	public void addArc(String id, String pre, String post) {
		// check if origin is place or transition
		if (this.Places.containsKey(pre)) { // is from p to t

			Place p = this.Places.get(pre);
			if (this.Transitions.containsKey(post)) {
				Transition t = this.Transitions.get(post);
				t.Pre.put(id, p);
			} else {
				throw new NoSuchElementException(String.format("Transition with id %s not found.", post));
			}

		} else if (this.Transitions.containsKey(pre)) { // is from t to p
			Transition t = this.Transitions.get(pre);
			if (this.Places.containsKey(post)) {
				Place p = this.Places.get(post);
				t.Post.put(id, p);
			} else {
				throw new NoSuchElementException(String.format("Place with id %s not found.", post));
			}
		} else {
			throw new NoSuchElementException(String.format("No Place or Transition with id %s found.", pre));
		}
	}

    /**
     * Sets the number of tokens for a specific place in the Petrinet.
     *
     * @param id     The identifier of the place.
     * @param tokens The number of tokens to be set.
     * @throws NoSuchElementException If the place with the specified identifier is not found.
     */
	public void setTokens(String id, Integer tokens) {
		if (this.Places.containsKey(id)) {
			Place p = this.Places.get(id);
			p.Tokens = tokens;
		} else {
			throw new NoSuchElementException(String.format("Place with id %s not found", id));
		}
	}

    /**
     * Sets the initial marking of the Petrinet based on the current state of its places.
     */
	public void setInitialMarking() {
		for (Map.Entry<String, Place> entry : this.Places.entrySet()) {
			Place p = entry.getValue();
			this.InitialMarking.add(p.Tokens);
		}
	}

    /**
     * Returns a string representation of the Petrinet, including details about its places, transitions, and arcs.
     *
     * @return A formatted string representing the Petrinet.
     */
    @Override
	public String toString() {
		Integer countPlaces = this.Places.size();
		Integer countTransitions = this.Transitions.size();
		Integer countArcs = 0;
		for (Map.Entry<String, Transition> entry : this.Transitions.entrySet()) {
			Transition t = entry.getValue();
			countArcs = countArcs + t.Pre.size() + t.Post.size();
		}
		String initPlace = String.format("[%s] %s", this.getID(this.InitialPlace), this.InitialPlace.Name);
		return String.format("\n\tPlaces:\t\t\t%d\n\tTransitions:\t\t%d\n\tArcs:\t\t\t%d"
				+ "\n\tInitital Place:\t\t%s\n\tInitial Marking: \t%s",
				countPlaces, countTransitions, countArcs, initPlace, this.getMarkingString());
	}
}

