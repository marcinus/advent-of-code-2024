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
            getNext(node, [-1, 0]),
    ].findAll { inMap(grid, it) }
}

static def getNext(def pos, def dir) {
    [pos[0] + dir[0], pos[1] + dir[1]]
}

static boolean inMap(int grid, def pos) {
    pos[0] >= 0 && pos[0] < grid &&
            pos[1] >= 0 && pos[1] < grid
}

static def find(sets, x) {
    while (sets[x][1] != x) {
        def parent = sets[x][1]
        sets[x][1] = sets[sets[x][1]][1]
        x = parent
    }
    x
}

static void union(sets, x, y) {
    x = find(sets, x)
    y = find(sets, y)

    if (x == y) return

    if (sets[x][0] < sets[y][0]) {
        def t = y
        y = x
        x = t
    }

    sets[y][1] = x

    if (sets[x][0] == sets[y][0]) {
        sets[x][0]++
    }
}

static def createInitialDisjointSet(List<List<Integer>> points, int grid) {
    def sets = [:]
    grid.times { i ->
        grid.times { j ->
            def p = [i, j]
            if (!points.contains(p) && !sets.containsKey(p)) {
                def visited = bfs(points, grid, p)
                visited.each {
                    sets[it] = [0, it]
                }
                visited.inject(visited[0]) { prev, e ->
                    union(sets, prev, e)
                    e
                }
            }
        }
    }
    sets
}

static def bfs(List<List<Integer>> points, int grid, def start) {
    def queue = [start] as Queue
    def visited = [start]
    while (!queue.isEmpty()) {
        def el = queue.poll()
        neighbours(grid, el).findAll { !points.contains(it) && !visited.contains(it) }.each {
            visited.add(it)
            queue.add(it)
        }
    }
    visited
}

static String solve(List<String> input, int steps = 1024, int grid = 71) {
    def points = input.collect { it.split(',').collect { it as int } }

    def sets = createInitialDisjointSet(points, grid)
    def pointsSoFar = points as Set
    points.reversed().find {
        sets[it] = [0, it]
        neighbours(grid, it).findAll { n -> !pointsSoFar.contains(n) }.each { n ->
            union(sets, it, n)
        }
        pointsSoFar.removeElement(it)
        find(sets, [0,0]) == find(sets, [grid-1, grid-1])
    }.join(',')
}


assert '6,1' == solve(sampleInput.split('\n') as List, 12, 7)
println solve(new File('input/day18.txt').readLines())