package propra.model;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Represents a transition in a Petri net. This class contains information about
 * the transition's name, its pre- and post-conditions (places), and its position.
 */
public class Transition implements Serializable {
	private static final long serialVersionUID = 5940391490002201945L;
    /**
     *  The name of the transition.
     */
    public String Name;

    /**
     * A map of pre-conditions (places) associated with this transition.
     */
    public LinkedHashMap<String, Place> Pre;

    /**
     *  A map of post-conditions (places) associated with this transition.
     */
    public LinkedHashMap<String, Place> Post;

    /**
     *  The position of the transition, represented as an array of two integers.
     */
    public Integer[] Position;

    /**
     * Constructs a Transition with the specified name.
     *
     * @param name The name of the transition.
     */
	public Transition(String name) {
		this.Name = name;
		this.Pre = new LinkedHashMap<>();
		this.Post = new LinkedHashMap<>();
		this.Position = new Integer[2];
	}

    /**
     * Checks if the transition is ready to fire. A transition is ready if all its pre-conditions (places)
     * have at least one token.
     *
     * @return true if the transition is ready to fire, false otherwise.
     */
	public boolean isReady() {
		// check if transition is ready to fire
		if (this.Pre.size() > 0) {
			for (Map.Entry<String, Place> entry : this.Pre.entrySet()) {
				Place p = entry.getValue();
				if (p.Tokens == 0) {
					return false; // not ready
				}
			}
		}
		return true; // ready
	}

    /**
     * Fires the transition. This method decreases the number of tokens in each pre-condition
     * place by one and increases the number of tokens in each post-condition place by one.
     */
	public void fire() {
		System.out.println("Transition - fire: Firing transition " + this.Name);
		if (this.Pre.size() > 0) {
			for (Map.Entry <String, Place> entry : this.Pre.entrySet()) {
				Place p =  entry.getValue();
				if (p.Tokens > 0) {
					p.Tokens--;
				} else {
					System.out.println("Transition - fire: Tried to remove token from empty place. Request ignored, but something is seriously wrong here!");
				}
			}
		} // removed tokens from origins

		if (this.Post.size() > 0) {
			for (Map.Entry <String, Place> entry : this.Post.entrySet()) {
				Place p =  entry.getValue();
				p.Tokens++;
			}
		} // added tokens to destinations
	}



}
