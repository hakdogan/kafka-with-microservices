package jugistanbul.entity;

/**
 * @author hakdogan (hakdogan@kodcu.com)
 * Created on 18.08.2020
 **/

public class EventObject
{
    private Integer customerId;
    private Integer productId;
    private Integer amount;
    private boolean inStock;
    private String cardNumber;
    private boolean isNumberValid;

    public EventObject(){}

    public EventObject(Integer customerId, Integer productId, Integer amount, boolean inStock, String cardNumber, boolean isNumberValid) {
        this.customerId = customerId;
        this.productId = productId;
        this.amount = amount;
        this.inStock = inStock;
        this.cardNumber = cardNumber;
        this.isNumberValid = isNumberValid;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public boolean isInStock() {
        return inStock;
    }

    public void setInStock(boolean inStock) {
        this.inStock = inStock;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public boolean isNumberValid() {
        return isNumberValid;
    }

    public void setNumberValid(boolean numberValid) {
        isNumberValid = numberValid;
    }

    @Override
    public String toString() {
        return "EventObject{" +
                "customerId=" + customerId +
                ", productId=" + productId +
                ", amount=" + amount +
                ", inStock=" + inStock +
                ", cardNumber='" + cardNumber + '\'' +
                ", isNumberValid=" + isNumberValid +
                '}';
    }
}
