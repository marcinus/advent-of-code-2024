def sampleInput = '''2333133121414131402'''

static long sumOfIndexes(int startIndex, int size) {
    (2 * startIndex + size - 1) * (size as long) / 2L
}

static long solve(String input) {
    def data = input.trim().collect {it as int }
    int endPointer = input.size() % 2 == 1 ? input.size()-1 : input.size()-2
    int startPointer = 0
    int sizePointer = 0
    int remainingEndItems = 0
    long checksum = 0
    while (endPointer > startPointer) { // TODO
        checksum += sumOfIndexes(sizePointer, data[startPointer]) * (startPointer / 2)
        sizePointer += data[startPointer]
        // now we fill the empty space
        int emptySpaceSize = data[startPointer+1]
        // while there is some space and not all the space was defragmented, proceed
        while(emptySpaceSize > 0 && endPointer > startPointer) {
            if (remainingEndItems > 0) {
                // add remaining end items
                if (emptySpaceSize < remainingEndItems) {
                    checksum += sumOfIndexes(sizePointer, emptySpaceSize) * (endPointer / 2)
                    sizePointer += emptySpaceSize
                    remainingEndItems -= emptySpaceSize
                    emptySpaceSize = 0
                } else {
                    checksum += sumOfIndexes(sizePointer, remainingEndItems) * (endPointer / 2)
                    sizePointer += remainingEndItems
                    emptySpaceSize -= remainingEndItems
                    remainingEndItems = 0
                    endPointer -= 2
                }
            } else {
                remainingEndItems = data[endPointer]
            }
        }
        startPointer += 2
    }
    if(remainingEndItems > 0) {
        checksum += sumOfIndexes(sizePointer, remainingEndItems) * (endPointer / 2)
    }
    checksum
}

assert 1928L == solve(sampleInput)
println solve(new File('input/day9.txt').text)