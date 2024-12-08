def sampleInput = '''............
........0...
.....0......
.......0....
....0.......
......A.....
............
............
........A...
.........A..
............
............'''

static def read(List<String> input) {
    int n = input.size()
    int m = input[0]?.size() ?: 0
    def antennas = [:].withDefault { [] }
    assert input.every { it.size() == m }
    for (int i = 0; i < n; i++) {
        for (int j = 0; j < m; j++) {
            def element = input[i][j]
            if (element == '.') continue
            antennas[element] << [i, j]
        }
    }
    [n, m, antennas]
}

static def getAntinodes(def posA, def posB) {
    [
            [2*posA[0]-posB[0], 2*posA[1]-posB[1]],
            [2*posB[0]-posA[0], 2*posB[1]-posA[1]]
    ]
}

static def getAntinodesInMap(int n, int m, def posA, def posB) {
    getAntinodes(posA, posB).findAll {
        it[0] >= 0 && it[0] < n &&
        it[1] >= 0 && it[1] < m
    }
}

static long solve(List<String> input) {
    def antinodes = [] as Set
    def (n, m, antennas) = read(input)
    antennas.each { type, positions ->
        int l = positions.size()
        for (int i = 0; i < l; i++) {
            for (int j = i+1; j < l; j++) {
                antinodes.addAll(getAntinodesInMap(n, m, positions[i], positions[j]))
            }
        }
    }
    antinodes.size()
}

assert 14L == solve(sampleInput.split('\n') as List)
println solve(new File('input/day8.txt').readLines())