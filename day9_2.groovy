def sampleInput = '''2333133121414131402'''

static long sumOfIndexes(int startIndex, int size) {
    (2 * startIndex + size - 1) * (size as long) / 2L
}

static long checksumOfChunk(int startIndex, int size, int id) {
    sumOfIndexes(startIndex, size) * id
}

static long solve(String input) {
    def data = input.trim().collect {it as int }
    int endPointer = input.size() % 2 == 1 ? input.size()-1 : input.size()-2
    def sizePointer = 0
    def checksum = 0
    def moved = data.collect { false }
    for (int i = 1; i < data.size(); i += 2) {
        if (!moved[i-1]) {
            checksum += checksumOfChunk(sizePointer, data[i-1], ((i-1)/2) as int)
        }
        sizePointer += data[i-1]
        def emptySpace = data[i]
        for (int j = endPointer; j > i; j -= 2) {
            if(moved[j]) continue
            def candidate = data[j]
            if (candidate <= emptySpace) {
                checksum += checksumOfChunk(sizePointer, candidate, j/2 as int)
                sizePointer += candidate
                emptySpace -= candidate
                moved[j] = true
            }
        }
        sizePointer += emptySpace
    }
    checksum
}

assert 2858L == solve(sampleInput)
println solve(new File('input/day9.txt').text)