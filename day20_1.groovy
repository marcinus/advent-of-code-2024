def sampleInput = '''###############
#...#...#.....#
#.#.#.#.#.###.#
#S#...#.#.#...#
#######.#.#.###
#######.#.#...#
#######.#.###.#
###..E#...#...#
###.#######.###
#...###...#...#
#.#####.#.###.#
#.#...#.#.#...#
#.#.#.#.#.#.###
#...#...#...###
###############'''

static boolean inMap(List<String> map, def pos) {
    pos[0] >= 0 && pos[0] < map.size() &&
    pos[1] >= 0 && pos[1] < map[0].size()
}

static boolean reachable(List<String> map, def pos) {
    map[pos[0]][pos[1]] != '#'
}

static def bfs(List<String> map, def endNode) {
    def distances = [(endNode): 0]
    def queue = [endNode] as Queue
    while(!queue.isEmpty()) {
        def node = queue.poll()
        [[0, -1], [1, 0], [-1, 0], [0,1]].each {
            def next = [node[0]+it[0], node[1]+it[1]]
            if (inMap(map, next) && reachable(map, next) && !distances.containsKey(next)) {
                queue.add(next)
                distances[next] = distances[node]+1
            }
        }
    }
    distances
}

static def getGainsFrom(List<String> map, Map distances, def pos) {
    [[0, -1], [1, 0], [-1, 0], [0,1]].collect {
        println pos
        def next = [pos[0]+it[0], pos[1]+it[1]]
        def nextToNext = [next[0]+it[0], next[1]+it[1]]
        println "$next $nextToNext"
        if (inMap(map, next) && !reachable(map, next) && inMap(map, nextToNext) && reachable(map, nextToNext)) {
            def gain = distances[nextToNext] - distances[pos] - 2
            return gain > 0 ? gain : 0
        }
        return 0
    }
}

static def getGains(List<String> map, Map distancesFromEnd, def shortestPathsNodes) {
    shortestPathsNodes.collectMany { pos ->
        getGainsFrom(map, distancesFromEnd, pos)
    }
}

static long solve(List<String> map, int min = 0) {
    def start, end
    map.size().times { i ->
        map[0].size().times { j ->
            if(map[i][j] == 'S') start = [i, j]
            else if(map[i][j] == 'E') end = [i, j]
        }
    }
    def distancesFromStart = bfs(map, start)
    def distancesFromEnd = bfs(map, end)
    def shortestPathLength = distancesFromEnd[start]
    def shortestPathsNodes = distancesFromStart.keySet().findAll { distancesFromStart[it] + distancesFromEnd[it] == shortestPathLength }
    def gains = getGains(map, distancesFromEnd, shortestPathsNodes)
    gains.findAll { it >= min }.size()
}


assert 340L == solve(sampleInput.split('\n') as List)
println solve(new File('input/day20.txt').readLines(), 100)