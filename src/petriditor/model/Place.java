package propra.model;

import java.io.Serializable;

/**
 *  A class storing the properties of a place inside a Petrinet.
 */

public class Place implements Serializable {
	private static final long serialVersionUID = 2020194324815817879L;

    /**
     * The number of tokens present in the place.
     */

	public Integer Tokens;

    /**
     * The name of the place.
     */
	public String Name;

    /**
     * The position of the place.
     * Position is represented by an array of Integers, where Position[0] is the X-coordinate
     * and Position[1] is the Y-coordinate.
     */
	public Integer[] Position;

    /**
     * Constructs a Place object with the given initial token count and name.
     *
     * @param token The initial number of tokens in the place.
     * @param name  The name of the place.
     */
	public Place(final int token, final String name) {
		this.Tokens = token;
		this.Name = name;
		this.Position = new Integer[2];
	}
}
