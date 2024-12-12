def sampleInput = '''RRRRIICCFF
RRRRIICCCF
VVRRRCCFFF
VVRCCCJFFF
VVVVCJJCFE
VVIVCCJJEE
VVIIICJJEE
MIIIIIJJEE
MIIISIJEEE
MMMISSJEEE'''

static def inMap(def pos, List<String> map) {
    pos[0] >= 0 && pos[0] < map.size() &&
    pos[1] >= 0 && pos[1] < map[0].size()
}

static def getNeighbours(def pos) {
    def DIRS = [[-1, 0], [0, -1], [1, 0], [0, 1]]
    DIRS.collect {
        [pos[0]+it[0], pos[1]+it[1]]
    }
}

static def get(List<String> map, def pos) {
    map[pos[0]][pos[1]]
}

static def bfs(def pos, List<String> map) {
    def visited = [] as Set
    def queue = [pos] as Queue
    def area = 0
    def fence = 0
    visited.add(pos)
    while (!queue.isEmpty()) {
        def next = queue.poll()
        area++
        getNeighbours(next).each {
            if (inMap(it, map)) {
                if (visited.contains(it)) return
                if (get(map, it) == get(map, pos)) {
                    visited.add(it)
                    queue.add(it)
                } else {
                    fence++
                }
            } else {
                fence++
            }
        }
    }
    [area, fence, visited]
}

static long solve(List<String> input) {
    def visited = [] as Set
    long cost = 0
    input.eachWithIndex { line, i ->
        line.eachWithIndex { it, j ->
            if(!visited.contains([i, j])) {
                def (area, fence, itemVisited) = bfs([i, j], input)
                visited.addAll(itemVisited)
                cost += area * fence
            }
        }
    }
    cost
}

assert 1930L == solve(sampleInput.split('\n') as List)
println solve(new File('input/day12.txt').readLines())