def sampleInput = '''#####
.####
.####
.####
.#.#.
.#...
.....

#####
##.##
.#.##
...##
...#.
...#.
.....

.....
#....
#....
#...#
#.#.#
#.###
#####

.....
.....
#.#..
###..
###.#
###.#
#####

.....
.....
.....
#....
#.#..
#.#.#
#####'''

static def parseKeyOrLock(List<String> input) {
    assert input.size() == 7
    assert input.every { it.size() == 5 }
    boolean isLock = input[0].every { it == '#' }
    def sizes = (0..<5).collect { i ->
        (0..<7).count { input[it][i] == '#' } - 1
    }
    [isLock, sizes]
}

static boolean matches(List lock, List key) {
    assert lock.size() == key.size()
    lock.withIndex().every { l, i ->
        key[i] + l <= 5
    }
}

static long solve(String input) {
    def locksAndKeys = input.split('\n\n').collect {
        parseKeyOrLock(it.split('\n') as List)
    }
    def locks = locksAndKeys.findAll { it[0] }.collect { it[1] }
    def keys = locksAndKeys.findAll { !it[0] }.collect { it[1] }
    locks.collect { l ->
        keys.count { k ->
            matches(l, k)
        }
    }.sum() as long
}

assert 3 == solve(sampleInput)
println solve(new File('input/day25.txt').text)