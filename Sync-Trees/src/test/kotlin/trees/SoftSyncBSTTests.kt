package trees

import org.example.trees.SoftSyncBST

class SoftSyncBSTTests : GeneralTests<SoftSyncBST<Int, String>>(
    treeType = { SoftSyncBST() }
)

