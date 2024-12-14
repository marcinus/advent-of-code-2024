def sampleInput = '''p=0,4 v=3,-3
p=6,3 v=-1,-3
p=10,3 v=-1,2
p=2,0 v=2,-1
p=0,0 v=1,3
p=3,0 v=-2,-2
p=7,6 v=-1,-3
p=3,0 v=-1,-2
p=9,3 v=2,3
p=7,3 v=-1,2
p=2,4 v=2,-3
p=9,5 v=-3,-3'''

static def parse(List<String> input) {
    def ROBOT_PATTERN = ~/p=(-?\d+),(-?\d+) v=(-?\d+),(-?\d+)/
    input.collect {
        def match = (it =~ ROBOT_PATTERN)[0]
        [
                [match[1] as int, match[2] as int],
                [match[3] as int, match[4] as int]
        ]
    }
}

static def newPos(def p, def v, int k, int n, int m) {
    [
            Math.floorMod(p[0] + k * v[0], m),
            Math.floorMod(p[1] + k * v[1], n)
    ]
}

static void printMap(def data, int n, int m) {
    System.out.print("\033[H\033[2J");
    System.out.flush();
    StringBuilder builder = new StringBuilder()
    n.times { i ->
        m.times { j ->
            builder.append(data.contains([j, i]) ? 'X' : '.')
        }
        builder.append('\n')
    }
    println builder.toString()
}


static def move(def pos, def dir) {
    [pos[0]+dir[0], pos[1]+dir[1]]
}

static long maxCluster(def map) {
    def visited = [] as Set
    map.collect { pos ->
        def clusterSize = 0
        def queue = [pos] as Queue
        while(!queue.isEmpty()) {
            def node = queue.poll()
            if(visited.contains(node)) continue
            clusterSize++
            visited.add(node)
            [[-1, 0], [1, 0], [0, -1], [0, 1]].each {
                def next = move(node, it)
                if (map.contains(next)) {
                    queue.add(next)
                }
            }
        }
        clusterSize
    }.max() as long
}

static void solve(List<String> input, int k = 6600, int n = 103, int m = 101) {
    def data = parse(input)
    (k..(k+1000)).each {l ->
        def newData = data.collect {newPos(it[0], it[1], l, n, m) }.unique()
        def clusterSize = maxCluster(newData)
        if (clusterSize > 200) {
            printMap(newData, n, m)
        }
    }

}

solve(new File('input/day14.txt').readLines())