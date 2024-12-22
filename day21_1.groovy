def sampleInput = '''029A
980A
179A
456A
379A'''

static def neighbours(state) {
    def movesNumeric = [
            '7': [
                    'v': '4',
                    '>': '8'
            ],
            '8': [
                    '<': '7',
                    'v': '5',
                    '>': '9'
            ],
            '9': [
                    'v': '6',
                    '<': '8'
            ],
            '4': [
                    '^': '7',
                    'v': '1',
                    '>': '5'
            ],
            '5': [
                    '^': '8',
                    '<': '4',
                    'v': '2',
                    '>': '6',
            ],
            '6': [
                    '^': '9',
                    'v': '3',
                    '<': '5'
            ],
            '1': [
                    '^': '4',
                    '>': '2'
            ],
            '2': [
                    '^': '5',
                    '<': '1',
                    'v': '0',
                    '>': '3',
            ],
            '3': [
                    '^': '6',
                    'v': 'A',
                    '<': '2'
            ],
            '0': [
                    '^': '2',
                    '>': 'A'
            ],
            'A': [
                    '^': '3',
                    '<': '0'
            ]
    ]

    def movesArrows = [
            'A': [
                    'v': '>',
                    '<': '^'
            ],
            '^': [
                    '>': 'A',
                    'v': 'v'
            ],
            '>': [
                    '<': 'v',
                    '^': 'A'
            ],
            'v': [
                    '>': '>',
                    '^': '^',
                    '<': '<'
            ],
            '<': [
                    '>': 'v',
            ]
    ]
    // case 'A':
    def n = [:]
    ['A', '<', '>', '^', 'v'].each {
        if (it == 'A') {
            if (state[0] == 'A') {
                if (state[1] == 'A') {
                    // do nothing, the button is pressed and nothing changes
                } else {
                    def nextNumber = movesNumeric[state[2]][state[1]]
                    if (nextNumber) {
                        n << [(it): [state[0], state[1], nextNumber]]
                    }
                }
            } else {
                def next2 = movesArrows[state[1]][state[0]]
                if (next2) {
                    n << [(it): [state[0], next2, state[2]]]
                }
            }
        } else {
            def next1 = movesArrows[state[0]][it]
            if (next1) {
                n << [(it): [next1, state[1], state[2]]]
            }
        }
    }
    n
}

//state has 1 numeric and 2 arrows
static def shortestNumberOfPushes(def state, def targetState) {
    // there's always an option to press A, or to press any of the buttons.
    def queue = [state] as Queue
    def dist = [(state): 0L]
    def prev = [:]
    while (!queue.isEmpty()) {
        def node = queue.poll()
        //println "$node"
        if (node == targetState) return [prev, dist[node]]
        neighbours(node).each {
            //println it
            if (!dist.containsKey(it.value)) {
                prev[it.value] = [it.key, node]
                queue.add(it.value)
                dist[it.value] = dist[node] + 1
            }
        }
    }
    throw new RuntimeException("Path not found between $state and $targetState")
}

static long numericPartOfCode(String code) {
    code.replaceAll(~/^0+/, '').replaceAll('A', '').toLong()
}

static long solve(List<String> inputs) {
    inputs.collect { code ->
        def state = ['A', 'A', 'A']
        def shortestPaths = code.inject(0) { sum, sign ->
            def (prev, shortestPath) = shortestNumberOfPushes(state, ['A', 'A', sign])
            state = ['A', 'A', sign]
            sum += shortestPath + 1
            sum
        }
        //println "$shortestPaths: ${numericPartOfCode(code)}"
        shortestPaths * numericPartOfCode(code)
    }.sum() as long
}


assert 126384L == solve(sampleInput.split('\n') as List)
println solve(new File('input/day21.txt').readLines())