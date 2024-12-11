def sampleInput = '''125 17'''

static List<Long> splitDigits(long element) {
    int digitsNo = digits(element)
    long divider = 10 ** (digitsNo / 2)
    long leftPart = element / divider
    long rightPart = element % divider
    [leftPart, rightPart]
}

static int digits(long element) {
    if (element == 0) return 1
    int digits = 0
    while(element > 0) {
        element /= 10
        digits += 1
    }
    digits
}

static long solve(String input) {
    def ITERATIONS = 25
    def list = input.split(' ').collect { it as long } as LinkedList
    ITERATIONS.times {
        def iterator = list.listIterator()
        while (iterator.hasNext()) {
            def element = iterator.next()
            if (element == 0) iterator.set(1)
            else if(digits(element) % 2 == 0) {
                def (digitsA, digitsB) = splitDigits(element)
                iterator.set(digitsA)
                iterator.add(digitsB)
            } else {
                iterator.set(element * 2024)
            }
        }
    }
    list.size()
}

assert 55312L == solve(sampleInput)
println solve(new File('input/day11.txt').text)