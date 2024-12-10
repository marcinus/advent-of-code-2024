def sampleInput = '''89010123
78121874
87430965
96549874
45678903
32019012
01329801
10456732
'''

static boolean inMap(int x, int y, int[][] map) {
    x >= 0 && x < map.length &&
    y >= 0 && y < map[0].length
}

static long bfs(int x, int y, int[][] map) {
    def DIRS = [[-1, 0], [1, 0], [0, -1], [0, 1]]
    def tops = [] as Set
    def queue = [[x, y]] as Queue
    while(!queue.isEmpty()) {
        def pos = queue.poll()
        if(map[pos[0]][pos[1]] == 9) {
            tops.add(pos)
            continue
        }
        DIRS.each { dir ->
            def next = [pos[0]+dir[0], pos[1]+dir[1]]
            if (inMap(next[0], next[1], map) && map[pos[0]][pos[1]]+1 == map[next[0]][next[1]]) {
                queue.add(next)
            }
        }
    }
    tops.size()
}

static int[][] parse(List<String> input) {
    int[][] map = new int[input.size()][input[0].size()]
    input.eachWithIndex { line, i ->
        line.eachWithIndex { it, j ->
            map[i][j] = it as int
        }
    }
    map
}

static long solve(List<String> input) {
    int[][] map = parse(input)
    (0..<map.length).collect { i ->
        (0..<(map[0].length)).collect {j ->
            map[i][j] == 0 ? bfs(i, j, map) : 0
        }.sum()
    }.sum() as long
}

assert 36L == solve(sampleInput.split('\n') as List)
println solve(new File('input/day10.txt').readLines())