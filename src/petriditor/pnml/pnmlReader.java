package propra.pnml;
import java.io.File;
import java.util.NoSuchElementException;

import propra.model.Petrinet;

/**
 * The pnmlReader class extends PNMLWopedParser and is responsible for parsing
 * PNML files into Petrinet objects.
 */
public class pnmlReader extends PNMLWopedParser{
	/*
	 * The Petrinet object to be created while parsing
	 */
	public Petrinet p;

    /**
     * Constructor for pnmlReader.
     *
     * @param pnmlFile The PNML file to be parsed.
     */
	public pnmlReader(File pnmlFile) {
		super(pnmlFile);
		this.p = new Petrinet();

	}

    /**
     * Parses the PNML file into a Petrinet object.
     *
     * @return The Petrinet object generated from the PNML file.
     * @throws NoSuchElementException If there is an issue during parsing.
     */
	public Petrinet parsePNML() throws NoSuchElementException {
		super.parse();
		this.p.InitialPlace = this.p.Places.entrySet().iterator().next().getValue();
		this.p.sortPlaces();
		this.p.setInitialMarking();
		return this.p;
	}

	private void errorHandler(NoSuchElementException e) {
		throw new NoSuchElementException(e);
	}

    /**
     * Adds a new transition to the Petrinet with the specified identifier.
     *
     * @param id The identifier for the new transition.
     */
	@Override
	public void newTransition(final String id) {
		this.p.addTransition(id);
	}

    /**
     * Adds a new place to the Petrinet with the specified identifier.
     *
     * @param id The identifier for the new place.
     */
	@Override
	public void newPlace(final String id) {
		this.p.addPlace(id, 0);
	}

    /**
     * Adds a new arc between two elements in the Petrinet.
     *
     * @param id     The identifier for the new arc.
     * @param source The source identifier of the arc.
     * @param target The target identifier of the arc.
     * @throws NoSuchElementException If an error occurs while adding the arc.
     */
	@Override
	public void newArc(final String id, final String source, final String target) {
		try {
			this.p.addArc(id, source, target);
		} catch (NoSuchElementException e) {
			System.err.println("Fehler beim Parsen des PNML Dokuments. "
					+ e.getMessage());
			e.printStackTrace();
			this.errorHandler(e);
		}

	}

    /**
     * Sets the position of an element in the Petrinet.
     *
     * @param id The identifier of the element.
     * @param x  The x-coordinate of the position.
     * @param y  The y-coordinate of the position.
     * @throws NoSuchElementException If an error occurs while setting the position.
     */
	@Override
	public void setPosition(final String id, final String x, final String y) {
		Integer[] pos = new Integer[2];
		pos[0] = Integer.parseInt(x);
		pos[1] = Integer.parseInt(y);
		try {
			this.p.setPosition(id, pos);
		} catch (NoSuchElementException e){
			System.err.println("Fehler beim Parsen des PNML Dokuments. "
					+ e.getMessage());
			e.printStackTrace();
			this.errorHandler(e);
		}
	}

    /**
     * Sets the name of an element in the Petrinet.
     *
     * @param id   The identifier of the element.
     * @param name The name to be set for the element.
     * @throws NoSuchElementException If an error occurs while setting the name.
     */
	@Override
	public void setName(final String id, final String name) {
		try {
			this.p.setName(id, name);
		} catch (NoSuchElementException e) {
			System.err.println("Fehler beim Parsen des PNML Dokuments. "
					+ e.getMessage());
			e.printStackTrace();
			this.errorHandler(e);
		}
	}

    /**
     * Sets the number of tokens for a place in the Petrinet.
     *
     * @param id     The identifier of the place.
     * @param tokens The number of tokens to be set.
     * @throws NoSuchElementException If an error occurs while setting the tokens.
     */
	@Override
	public void setTokens(final String id, final String tokens) {
		try {
			this.p.setTokens(id, Integer.parseInt(tokens));
		} catch (NoSuchElementException e) {
			System.err.println("Fehler beim Parsen des PNML Dokuments. "
					+ e.getMessage());
			e.printStackTrace();
			this.errorHandler(e);
		}
	}
}
