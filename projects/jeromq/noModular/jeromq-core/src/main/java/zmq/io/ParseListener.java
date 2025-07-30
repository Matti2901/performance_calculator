package zmq.io;

/**
 * Call backs during parsing process
 */
public interface ParseListener
{
    /**
     * Called when a property has been parsed.
     * @param name the name of the property.
     * @param value the value of the property.
     * @param valueAsString the value in a string representation.
     * @return 0 to continue the parsing process, any other value to interrupt it.
     */
    int parsed(String name, byte[] value, String valueAsString);
}
