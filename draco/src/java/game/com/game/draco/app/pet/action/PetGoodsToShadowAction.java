package com.game.draco.app.pet.action;

import com.game.draco.GameContext;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.request.C1661_PetGoodsToShadowReqMessage;
import com.game.draco.message.response.C0002_ErrorRespMessage;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.app.goods.behavior.AbstractGoodsBehavior;
import sacred.alliance.magic.app.goods.behavior.GoodsBehaviorType;
import sacred.alliance.magic.app.goods.behavior.param.UseGoodsParam;
import sacred.alliance.magic.base.GoodsType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.vo.RoleInstance;

public class PetGoodsToShadowAction extends BaseAction<C1661_PetGoodsToShadowReqMessage> {

	@Override
	public Message execute(ActionContext context, C1661_PetGoodsToShadowReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if (null == role) {
			return null;
		}
		String goodsInstanceId = reqMsg.getGoodsInstanceId();
		RoleGoods roleGoods = role.getRoleBackpack().getRoleGoodsByInstanceId(goodsInstanceId);
		// 如果没有该物品
		if (null == roleGoods) {
			return new C0002_ErrorRespMessage(reqMsg.getCommandId(), this.getText(TextId.NO_GOODS));
		}
		// 如果物品不是宠物
		GoodsType goodsType = GameContext.getGoodsApp().getGoodsType(roleGoods.getGoodsId());
		if (GoodsType.GoodsPet != goodsType) {
			return new C0002_ErrorRespMessage(reqMsg.getCommandId(), this.getText(TextId.ERROR_INPUT));
		}
		AbstractGoodsBehavior behavior = goodsType.getGoodsBehavior(GoodsBehaviorType.Use);
		if (null == behavior) {
			return new C0003_TipNotifyMessage(Status.GOODS_NO_USE.getTips());
		}
		UseGoodsParam param = new UseGoodsParam(role);
		param.setRoleGoods(roleGoods);
		param.setConfirm(true);
		Result result = behavior.operate(param);
		return new C0003_TipNotifyMessage(result.getInfo());
	}

}
