import groovy.transform.Memoized

def sampleInput = '''029A
980A
179A
456A
379A'''


@Memoized
static long shortestPath(def lookup, String prevState, String state, int i) {
    if (i == 0) return 0L
    if(prevState == state) return 0L
    //println "$prevState to $state at $i has ${lookup[prevState][state][0].size()} steps"
    if (i == 1) return lookup[prevState][state][0].size()
    if(prevState == state) return 0L
    def res = lookup[prevState][state].collect { List<String> variant ->
        //println "Variant: $prevState $state $i $variant"
        def cost = 0
        def lastStep = variant.inject('A') { prev, step ->
            def localCost = shortestPath(lookup, prev, step, i-1) + 1
            //println "Traversing from $prevState to $state at $i. Path between $prev and $step costs ${localCost-1} + 1 for click at ${i-1}"
            cost += localCost
            step
        }
        def totalCost = cost + shortestPath(lookup, lastStep, 'A', i-1)
        //println "Traversing from $prevState to $state at $i. Total cost is $totalCost"
        totalCost

    }.collect { /*println "Found path between $prevState and $state at $i with $it"; */ it}.min() as long
    //println "$prevState to $state at $i is $res"
    res
}

static def lookup(List<String> numberBoard) {
    def numberLookup = [:]
    numberBoard.eachWithIndex { line, i ->
        line.eachWithIndex { String number, int j ->
            if (number != ' ') {
                numberLookup[number] = [i, j]
            }
        }
    }
    numberLookup
}

//state has 1 numeric and 2 arrows
static def shortestPathBetween(def movesArrows, def lookup, def state, def targetState, int arrowBoards = 25) {
    def A = lookup[state]
    def B = lookup[targetState]
    def vertical = (A[0] > B[0] ? '^' : 'v')
    def verticalSteps = Math.abs(A[0]-B[0])
    def horizontal = (A[1] > B[1] ? '<' : '>')
    def horizontalSteps = Math.abs(A[1]-B[1])

    //println "V: $verticalSteps H: $horizontalSteps"
    if(A[0]==B[0]) {
        def aToHorizontal = shortestPath(movesArrows, 'A', horizontal, arrowBoards)
        def horizontalToA = shortestPath(movesArrows, horizontal, 'A', arrowBoards)

        //println "AHA: $aToHorizontal $horizontalToA"
        return aToHorizontal + horizontalToA + horizontalSteps + 1
        // only horizontal
    } else if(A[1] == B[1]) {
        def aToVertical = shortestPath(movesArrows, 'A', vertical, arrowBoards)
        def verticalToA = shortestPath(movesArrows, vertical, 'A', arrowBoards)

        //println "AVA: $aToVertical $verticalToA"
        return aToVertical + verticalToA + verticalSteps + 1
        // only vertical
    }

    def aToVertical = shortestPath(movesArrows, 'A', vertical, arrowBoards)
    def verticalToHorizontal = shortestPath(movesArrows, vertical, horizontal, arrowBoards)
    def horizontalToA = shortestPath(movesArrows, horizontal, 'A', arrowBoards)

    def aToHorizontal = shortestPath(movesArrows, 'A', horizontal, arrowBoards)
    def horizontalToVertical = shortestPath(movesArrows, horizontal, vertical, arrowBoards)
    def verticalToA = shortestPath(movesArrows, vertical, 'A', arrowBoards)


    def firstVertical = aToVertical + verticalToHorizontal + horizontalToA + verticalSteps + horizontalSteps + 1
    def firstHorizontal = aToHorizontal + horizontalToVertical + verticalToA + verticalSteps + horizontalSteps + 1

    //println "V->H: $aToVertical $verticalToHorizontal $horizontalToA = $firstVertical"
    //println "H->V: $aToHorizontal $horizontalToVertical $verticalToA = $firstHorizontal"

    if(A[0] == 3 && B[1] == 0) {
        // only first vertical then horizontal
        return firstVertical
    } else if (B[0] == 3 && A[1] == 0) {
        // only first horizontal then vertical
        return firstHorizontal
    } else {
        return Math.min(firstVertical, firstHorizontal)
        // minimum from both cases
    }
}

static long numericPartOfCode(String code) {
    code.replaceAll(~/^0+/, '').replaceAll('A', '').toLong()
}

static long solve(List<String> inputs, int arrowBoards = 25) {
    def movesArrows = [
            'A': [
                    '^': [['<']],
                    '>': [['v']],
                    'v': [['<', 'v'], ['v', '<']],
                    '<': [['v', '<', '<'], ['<','v', '<']]
            ],
            '^': [
                    'A': [['>']],
                    '>': [['v', '>'], ['>', 'v']],
                    'v': [['v']],
                    '<': [['v', '<']]
            ],
            '<': [
                    'A': [['>', '>', '^'], ['>', '^', '>']],
                    '>': [['>', '>']],
                    'v': [['>']],
                    '^': [['>', '^']]
            ],
            '>': [
                    'A': [['^']],
                    '<': [['<', '<']],
                    'v': [['<']],
                    '^': [['<', '^'], ['^', '<']]
            ],
            'v': [
                    'A': [[ '>', '^'], ['^', '>']],
                    '>': [['>']],
                    '^': [['^']],
                    '<': [['<']]
            ],

    ]
    def numbersLookup = lookup('''789
456
123
 0A'''.split('\n') as List<String>)
    def arrowsLookup = lookup(''' ^A
<v>'''.split('\n') as List<String>)
    inputs.collect { code ->
        def previousSign = 'A'
        def shortestPaths = code.inject(0) { sum, sign ->
            def shortestPath = shortestPathBetween(movesArrows, numbersLookup, previousSign, sign, arrowBoards)
            previousSign = sign
            sum += shortestPath
            sum
        }
        //println "$shortestPaths: ${numericPartOfCode(code)}"
        shortestPaths * numericPartOfCode(code)
    }.sum() as long
}


assert 126384L == solve(sampleInput.split('\n') as List, 2)
assert 154115708116294L == solve(sampleInput.split('\n') as List)
println solve(new File('input/day21.txt').readLines())