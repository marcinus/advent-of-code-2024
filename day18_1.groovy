def sampleInput = '''5,4
4,2
4,5
3,0
2,1
6,3
2,4
1,5
0,6
3,3
2,6
5,1
1,2
5,5
2,5
6,5
1,4
0,4
6,4
1,1
6,1
1,0
0,5
1,6
2,0'''

static def neighbours(int grid, def node) {
    [
            getNext(node, [0, 1]),
            getNext(node, [1, 0]),
            getNext(node, [0, -1]),
            getNext(node, [-1,0]),
    ].findAll { inMap(grid, it) }
}

static def getNext(def pos, def dir) {
    [pos[0] + dir[0], pos[1] + dir[1]]
}

static boolean inMap(int grid, def pos) {
    pos[0] >= 0 && pos[0] < grid &&
    pos[1] >= 0 && pos[1] < grid
}

static long bfs(List<List<Integer>> points, int grid) {
    def start = [0, 0]
    def queue = [start] as Queue
    def visited = [(start):0L]
    while (!queue.isEmpty()) {
        def el = queue.poll()
        neighbours(grid, el).findAll { !points.contains(it) && !visited.containsKey(it) }.each {
            visited[it] = visited[el] + 1
            queue.add(it)
        }
    }
    visited[[grid-1, grid-1]]
}

static long solve(List<String> input, int steps = 1024, int grid = 71) {
    def points = input.collect { it.split(',').collect { it as int } }.take(steps)

    bfs(points, grid)
}


assert 22L == solve(sampleInput.split('\n') as List, 12, 7)
println solve(new File('input/day18.txt').readLines())