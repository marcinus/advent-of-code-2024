def sampleInput = '''47|53
97|13
97|61
97|47
75|29
61|13
75|53
29|13
97|29
53|29
61|53
97|53
61|29
47|13
75|47
97|75
47|61
75|61
47|29
75|13
53|13

75,47,61,53,29
97,61,53,29,13
75,29,13
75,97,47,61,53
61,13,29
97,13,75,29,47
'''

static List<Integer> sort(Map<Integer, List<Integer>> conditions, List<Integer> update) {
    def newUpdate = []
    int n = update.size()
    for(int i = 0; i < n; i++) {
        // find an element that has no predecessor in the list
        def element = update.find {candidate -> update.every { !conditions[it]?.contains(candidate)} }
        newUpdate << element
        update.removeElement(element)
    }
    newUpdate
}

static long solve(List<String> input) {
    int divider = input.findIndexOf { it.isEmpty() }
    def conditions = input.take(divider).collect {it.split(/\|/).collect { it as int} }.groupBy { it[0] }.collectEntries {k,v -> [k, v.collect {it[1] } ] }
    def updates = input.drop(divider+1).collect { it.split(',').collect { it as int } }
    updates.findAll { update ->
        update.withIndex().any { element, index ->
            ((index+1)..<(update.size())).any { i ->
                (conditions[update[i]])?.contains(element)
            }
        }
    } .collect {
        sort(conditions, it) }
            .collect {
                it[(it.size()-1)/2 as int] }
            .sum() as long ?: 0

}

assert 123L == solve(sampleInput.split('\n') as List)
println solve(new File('input/day5.txt').readLines())