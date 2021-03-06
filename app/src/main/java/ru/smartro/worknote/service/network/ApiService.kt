package ru.smartro.worknote.service.network

import retrofit2.Response
import retrofit2.http.*
import ru.smartro.worknote.service.network.body.AuthBody
import ru.smartro.worknote.service.network.body.ProgressBody
import ru.smartro.worknote.service.network.body.WayListBody
import ru.smartro.worknote.service.network.body.breakdown.BreakdownBody
import ru.smartro.worknote.service.network.body.complete.CompleteWayBody
import ru.smartro.worknote.service.network.body.early_complete.EarlyCompleteBody
import ru.smartro.worknote.service.network.body.failure.FailureBody
import ru.smartro.worknote.service.network.body.served.ServiceResultBody
import ru.smartro.worknote.service.network.body.synchro.SynchronizeBody
import ru.smartro.worknote.service.network.response.EmptyResponse
import ru.smartro.worknote.service.network.response.auth.AuthResponse
import ru.smartro.worknote.service.network.response.breakdown.BreakDownResponse
import ru.smartro.worknote.service.network.response.breakdown.sendBreakDown.BreakDownResultResponse
import ru.smartro.worknote.service.network.response.cancelation_reason.CancelationReasonResponse
import ru.smartro.worknote.service.network.response.failure_reason.FailureReasonResponse
import ru.smartro.worknote.service.network.response.failure_reason.send_failure.FailureResultResponse
import ru.smartro.worknote.service.network.response.organisation.OrganisationResponse
import ru.smartro.worknote.service.network.response.served.ServedResponse
import ru.smartro.worknote.service.network.response.synchronize.SynchronizeResponse
import ru.smartro.worknote.service.network.response.vehicle.VehicleResponse
import ru.smartro.worknote.service.network.response.way_list.WayListResponse
import ru.smartro.worknote.service.network.response.work_order.WorkOrderResponse

interface ApiService {

    @POST("login")
    suspend fun auth(@Body model: AuthBody): Response<AuthResponse>

    @GET("owner")
    suspend fun getOwners(): Response<OrganisationResponse>

    @GET("vehicle")
    suspend fun getVehicle(@Query("o") organisationId: Int): Response<VehicleResponse>

    @GET("breakdown_type?page=all")
    suspend fun getBreakDownTypes(): Response<BreakDownResponse>

    @GET("failure_reason?page=all")
    suspend fun getFailReason(): Response<FailureReasonResponse>

    @POST("waybill")
    suspend fun getWayList(@Body body: WayListBody): Response<WayListResponse>

    @POST("breakdown")
    suspend fun sendBreakDown(@Body body: BreakdownBody): Response<BreakDownResultResponse>

    @POST("failure")
    suspend fun sendFailure(@Body body: FailureBody): Response<FailureResultResponse>

/*
    @POST("waybill/{id}")
    suspend fun getWayTask(@Path("id") wayId: Int, @Body wayTaskBody: WayTaskBody): Response<WayTaskResponse>
*/

    @POST("served")
    suspend fun served(@Body body: ServiceResultBody): Response<ServedResponse>

    @POST("workorder/{id}/progress")
    suspend fun progress(@Path("id") id: Int, @Body time: ProgressBody): Response<EmptyResponse>

    @POST("workorder/{id}/complete")
    suspend fun complete(@Path("id") id: Int, @Body time: CompleteWayBody): Response<EmptyResponse>

    @GET("work_order_cancelation_reason")
    suspend fun getCancelWayReason(): Response<CancelationReasonResponse>

    @GET("work_order_cancelation_reason")
    suspend fun getCancelWayReasonNoLv(): Response<CancelationReasonResponse>

    @POST("workorder/{id}/early_complete")
    suspend fun earlyComplete(@Path("id") id: Int, @Body body: EarlyCompleteBody): Response<EmptyResponse>

    @POST("synchro")
    suspend fun postSynchro(@Body time: SynchronizeBody): Response<SynchronizeResponse>

    @POST("synchro/{o_id}/{w_id}")
    suspend fun getWorkOrder(@Path("o_id") organisationId: Int, @Path("w_id") waybillId: Int): Response<WorkOrderResponse>

}