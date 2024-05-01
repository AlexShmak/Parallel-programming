package trees

import org.example.trees.HardSyncBST

class HardSyncBSTTests : GeneralTests<HardSyncBST<Int, String>>(
    treeType = { HardSyncBST() }
)

