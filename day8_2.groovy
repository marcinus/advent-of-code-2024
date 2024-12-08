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

static def inMap(int n, int m, def pos) {
    pos[0] >= 0 && pos[0] < n &&
    pos[1] >= 0 && pos[1] < m
}

static def getAntinodesPolarizedInMap(int n, int m, def posA, def posB) {
    def antinodes = [] as Set
    int k = 1
    while (true) {
        def antinode = [posA[0] + k * (posA[0] - posB[0]), posA[1] + k * (posA[1] - posB[1])]
        if (!inMap(n, m, antinode)) {
            break
        }
        antinodes << antinode
        k++
    }
    antinodes
}

static def getAntinodesInMap(int n, int m, def posA, def posB) {
    getAntinodesPolarizedInMap(n, m, posA, posB) +
    getAntinodesPolarizedInMap(n, m, posB, posA)
}

static long solve(List<String> input) {
    def antinodes = [] as Set
    def (n, m, antennas) = read(input)
    antennas.each { type, positions ->
        int l = positions.size()
        for (int i = 0; i < l; i++) {
            for (int j = i + 1; j < l; j++) {
                antinodes.addAll(getAntinodesInMap(n, m, positions[i], positions[j]))
            }
        }
    }
    antinodes.addAll(antennas.values().collectMany { it })
    antinodes.size()
}

assert 34L == solve(sampleInput.split('\n') as List)
println solve(new File('input/day8.txt').readLines())