package trees

import org.example.trees.OptimisticSyncBST

class OptimisticSyncBSTTests : GeneralTests<OptimisticSyncBST<Int, String>>(
    treeType = { OptimisticSyncBST() }
)