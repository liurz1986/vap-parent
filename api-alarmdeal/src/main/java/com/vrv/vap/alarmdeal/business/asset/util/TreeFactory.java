package com.vrv.vap.alarmdeal.business.asset.util;

import java.util.*;

public class TreeFactory {
	/**
	 * 将平面的treeNodes构造成树形结构的List<TreeNode>
	 * 
	 * @param treeNodes
	 *            平面的treeNode，children中为空
	 * @return 树形结构的treeNode，children中有值
	 */
	public static <T extends ITreeNode<T, ID>, ID> List<T> buildTree(Collection<T> treeNodes) {
		List<T> result = new ArrayList<>();
		Map<ID, T> tmp = new HashMap<>();
		for (T node : treeNodes) {
			tmp.put(node.getKey(), node);
			node.setChildren(new ArrayList<T>());
		}
		for (T cNode : treeNodes) {
			ID parentId = cNode.getParentId();
			T parentNode = tmp.get(parentId);
			if (parentNode != null) {
				parentNode.getChildren().add(cNode);
			} else {
				result.add(cNode);
			}
		}

		return result;
	}


	
	/**
	 * 将平面的treeNodes构造成树形结构的List<TreeNode>
	 * 
	 * @param treeNodes
	 *            平面的treeNode，children中为空
	 * @param topParentId
	 *            顶层父节点id。顶层不是这个id的将过滤掉
	 * @return 树形结构的treeNode，children中有值
	 */
	public static <T extends ITreeNode<T, ID>, ID> List<T> buildTree(Collection<T> treeNodes, String topParentId) {
		List<T> result = new ArrayList<>();
		Map<ID, T> tmp = new HashMap<>();
		for (T node : treeNodes) {
			tmp.put(node.getKey(), node);
			node.setChildren(new ArrayList<>());
		}

		for (T cNode : treeNodes) {
			ID parentId = cNode.getParentId();
			if (parentId != null && parentId.equals(topParentId)) {
				result.add(cNode);
			} else{
				T parentNode = tmp.get(parentId);
				if (parentNode != null) {
					parentNode.getChildren().add(cNode);
				}
			}
		}

		return result;
	}
}
