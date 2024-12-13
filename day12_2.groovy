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
        [[pos[0]+it[0], pos[1]+it[1]], it]
    }
}

static def get(List<String> map, def pos) {
    map[pos[0]][pos[1]]
}

static def move(pos, dir) {
    [pos[0]+dir[0], pos[1]+dir[1]]
}

static def countEdges(def fence) {
    def DIRS = [[-1, 0], [0, -1], [1, 0], [0, 1]]
    def visited = [] as Set
    fence.collect {
        if(visited.contains(it)) return 0
        def dir = it[1]
        visited.add(it)
        def dir1 = DIRS[(DIRS.indexOf(dir)+1)%4]
        def dir2 = DIRS[(DIRS.indexOf(dir)-1)%4]
        def current = it[0]
        while (fence.contains([move(current, dir1), dir])) {
            current = move(current, dir1)
            visited.add([current, dir])
        }
        current = it[0]
        while (fence.contains([move(current, dir2), dir])) {
            current = move(current, dir2)
            visited.add([current, dir])
        }
        1
    }.sum()
}

static def bfs(def pos, List<String> map) {
    def visited = [] as Set
    def queue = [pos] as Queue
    def area = 0
    def fence = []
    visited.add(pos)
    while (!queue.isEmpty()) {
        def next = queue.poll()
        area++
        getNeighbours(next).each {
            def neighbour = it[0]
            if (inMap(neighbour, map)) {
                if (visited.contains(neighbour)) return
                if (get(map, neighbour) == get(map, pos)) {
                    visited.add(neighbour)
                    queue.add(neighbour)
                } else {
                    fence.add([next, it[1]])
                }
            } else {
                fence.add([next, it[1]])
            }
        }
    }
    def edgeCount = countEdges(fence)
    [area, edgeCount, visited]
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

assert 1206L == solve(sampleInput.split('\n') as List)
println solve(new File('input/day12.txt').readLines())