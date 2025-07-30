package zmq.pipe;

//  Individual memory chunk to hold N elements.
class Chunk<T> {
    final T[] values;
    final int[] pos;
    Chunk<T> prev;
    Chunk<T> next;

    @SuppressWarnings("unchecked")
    public Chunk(int size, int memoryPtr) {
        values = (T[]) new Object[size];
        pos = new int[size];
        for (int i = 0; i != values.length; i++) {
            pos[i] = memoryPtr;
            memoryPtr++;
        }
    }
}
